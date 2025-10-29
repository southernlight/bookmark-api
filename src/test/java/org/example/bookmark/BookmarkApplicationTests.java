package org.example.bookmark;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.example.bookmark.dto.BookmarkCriteria;
import org.example.bookmark.dto.BookmarkResponse;
import org.example.bookmark.entity.Bookmark;
import org.example.bookmark.entity.Member;
import org.example.bookmark.entity.Tag;
import org.example.bookmark.repository.BookmarkRepository;
import org.example.bookmark.repository.MemberRepository;
import org.example.bookmark.repository.TagRepository;
import org.example.bookmark.service.BookmarkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

@SpringBootTest
@Transactional
class BookmarkApplicationTests {

  @Autowired
  private BookmarkService bookmarkService;

  @Autowired
  private BookmarkRepository bookmarkRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private TagRepository tagRepository;

  private Long member1Id;

  @BeforeEach
  void setUp() {

    memberRepository.deleteAll();
    bookmarkRepository.deleteAll();
    tagRepository.deleteAll();

    Member member1 = memberRepository.save(Member.builder()
        .email("user1@example.com")
        .password("password1")
        .build());

    Member member2 = memberRepository.save(Member.builder()
        .email("user2@example.com")
        .password("password2")
        .build());

    member1Id = member1.getId();

    // 태그를 미리 저장
    Tag tagSearch = tagRepository.save(Tag.builder().name("검색").build());
    Tag tagPortal = tagRepository.save(Tag.builder().name("포털").build());
    Tag tagAd = tagRepository.save(Tag.builder().name("광고").build());

    List<Bookmark> bookmarks = List.of(
        Bookmark.builder()
            .title("네이버")
            .url("https://naver.com")
            .memo("포털")
            .member(member1)
            .tags(Set.of(tagSearch))
            .build(),
        Bookmark.builder()
            .title("구글")
            .url("https://google.com")
            .memo("검색 엔진")
            .member(member1)
            .tags(Set.of(tagSearch, tagAd, tagPortal))
            .build(),
        Bookmark.builder()
            .title("다음")
            .url("https://daum.net")
            .memo("포털 사이트")
            .member(member1)
            .tags(Set.of(tagPortal))
            .build(),
        Bookmark.builder()
            .title("네이버2")
            .url("https://naver2.com")
            .memo("포털2")
            .member(member2)
            .tags(Set.of(tagSearch))
            .build()
    );

    bookmarkRepository.saveAll(bookmarks);
  }

  @Test
  void filterByTitle() {

    BookmarkCriteria criteria = new BookmarkCriteria();
    criteria.setTitle("네"); // 제목에 '네' 포함
    criteria.setPage(0);
    criteria.setSize(10);

    Page<BookmarkResponse> result = bookmarkService.getBookmarksPage(member1Id,criteria);

    assertEquals(1, result.getContent().size());
    assertEquals("네이버", result.getContent().get(0).getTitle());
  }

  @Test
  void filterByTag() {

    BookmarkCriteria criteria = new BookmarkCriteria();
    criteria.setTag("포털");
    criteria.setPage(0);
    criteria.setSize(10);

    Page<BookmarkResponse> result = bookmarkService.getBookmarksPage(member1Id,criteria);

    // tags '포털'을 가진 북마크 2개
    assertEquals(2, result.getContent().size());
    Set<String> titles = result.getContent().stream()
        .map(BookmarkResponse::getTitle)
        .collect(Collectors.toSet());
    assertTrue(titles.contains("구글"));
    assertTrue(titles.contains("다음"));
  }

  @Test
  void filterByTitleAndTag() {

    BookmarkCriteria criteria = new BookmarkCriteria();
    criteria.setTitle("다");
    criteria.setTag("포털");
    criteria.setPage(0);
    criteria.setSize(10);

    Page<BookmarkResponse> result = bookmarkService.getBookmarksPage(member1Id,criteria);

    assertEquals(1, result.getContent().size());
    assertEquals("다음", result.getContent().get(0).getTitle());
  }

  @Test
  void sortByCreatedAt() {
    BookmarkCriteria criteria = new BookmarkCriteria();
    criteria.setPage(0);
    criteria.setSize(10);
    criteria.setSort("createdAt");
    criteria.setDirection("desc");

    Page<BookmarkResponse> result = bookmarkService.getBookmarksPage(member1Id, criteria);

    List<LocalDateTime> createdAts = result.getContent().stream()
        .map(BookmarkResponse::getCreatedAt)
        .toList();

    for (LocalDateTime createdAt : createdAts) {
      System.out.println(createdAt);
    }

    assertFalse(createdAts.get(0).isBefore(createdAts.get(1)));
    assertFalse(createdAts.get(1).isBefore(createdAts.get(2)));

  }

  @Test
  void contextLoads() {
  }

}
