package org.example.bookmark.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.bookmark.common.mapper.BookmarkMapper;
import org.example.bookmark.common.exception.BusinessException;
import org.example.bookmark.common.exception.ErrorCode;
import org.example.bookmark.dto.BookmarkCriteria;
import org.example.bookmark.dto.BookmarkRequest;
import org.example.bookmark.dto.BookmarkResponse;
import org.example.bookmark.entity.Bookmark;
import org.example.bookmark.entity.Member;
import org.example.bookmark.entity.QBookmark;
import org.example.bookmark.entity.QTag;
import org.example.bookmark.entity.Tag;
import org.example.bookmark.repository.BookmarkRepository;
import org.example.bookmark.repository.MemberRepository;
import org.example.bookmark.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;
  private final MemberRepository memberRepository;
  private final TagRepository tagRepository;
  private final JPAQueryFactory queryFactory;

  @Transactional
  public BookmarkResponse registerBookmark(Long memberId,BookmarkRequest request) {

    Member member = findMemberById(memberId);

    Bookmark newBookmark = BookmarkMapper.toBookmarkEntity(request);
    Set<Tag> tags = getOrCreateTags(request.getTags());

    newBookmark.setMember(member);
    newBookmark.setTags(tags);

    Bookmark savedBookmark = bookmarkRepository.save(newBookmark);

    return BookmarkMapper.toBookmarkResponse(savedBookmark);
  }

  @Transactional
  public Page<BookmarkResponse> getBookmarksPage(Long memberId,BookmarkCriteria criteria) {

    QBookmark bookmark = QBookmark.bookmark;
    QTag tag = QTag.tag;

    BooleanExpression predicate = criteria.toPredicate(bookmark);
    predicate = predicate.and(bookmark.member.id.eq(memberId));

    List<Long> bookmarkIds = queryFactory
        .select(bookmark.id)
        .from(bookmark)
        .where(predicate)
        .offset(criteria.toPageable().getOffset())
        .limit(criteria.toPageable().getPageSize())
        .fetch();

    List<Bookmark> bookmarkList = queryFactory
        .selectFrom(bookmark)
        .leftJoin(bookmark.tags, tag).fetchJoin()
        .where(bookmark.id.in(bookmarkIds))
        .distinct()
        .fetch();

    long total = Optional.ofNullable(
        queryFactory.select(bookmark.count())
            .from(bookmark)
            .where(predicate)
            .fetchOne()
    ).orElse(0L);

    List<BookmarkResponse> bookmarkResponseList = BookmarkMapper.toBookmarkResponseList(bookmarkList);

    return new PageImpl<>(bookmarkResponseList,criteria.toPageable(),total);
  }

  @Transactional
  public BookmarkResponse getBookmarkById(Long memberId, Long bookmarkId) {

    Bookmark bookmark = findBookmarkById(bookmarkId);

    validateOwner(memberId, bookmark);

    return BookmarkMapper.toBookmarkResponse(bookmark);
  }

  @Transactional
  public BookmarkResponse updateBookmark(Long memberId,Long bookmarkId, BookmarkRequest request) {

    Bookmark bookmark = findBookmarkById(bookmarkId);

    validateOwner(memberId, bookmark);

    bookmark.setTitle(request.getTitle());
    bookmark.setUrl(request.getUrl());
    bookmark.setMemo(request.getMemo());

    Set<Tag> tags = getOrCreateTags(request.getTags());
    bookmark.setTags(tags);

    Bookmark updatedBookmark = bookmarkRepository.save(bookmark);
    return BookmarkMapper.toBookmarkResponse(updatedBookmark);
  }

  @Transactional
  public void deleteBookmark(Long memberId ,Long bookmarkId) {

    Bookmark bookmark = findBookmarkById(bookmarkId);

    validateOwner(memberId, bookmark);

    bookmarkRepository.delete(bookmark);
  }

  // ------------------- private helpers -------------------

  private Bookmark findBookmarkById(Long bookmarkId) {
    return bookmarkRepository.findById(bookmarkId)
        .orElseThrow(() -> new BusinessException(ErrorCode.BOOKMARK_NOT_FOUND));
  }

  private Member findMemberById(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
  }

  private void validateOwner(Long memberId, Bookmark bookmark) {
    if (!bookmark.getMember().getId().equals(memberId)) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
    }
  }

  // 추출된 private 메서드
  private Set<Tag> getOrCreateTags(List<String> tagNames) {
    if (tagNames == null) {
      return Collections.emptySet();
    }

    return tagNames.stream()
        .map(tagName -> tagRepository.findByName(tagName)
            .orElseGet(() -> {
              Tag newTag = new Tag();
              newTag.setName(tagName);
              return tagRepository.save(newTag);
            }))
        .collect(Collectors.toSet());
  }
}
