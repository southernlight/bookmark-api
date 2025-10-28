package org.example.bookmark.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.example.bookmark.common.exception.BusinessException;
import org.example.bookmark.common.exception.ErrorCode;
import org.example.bookmark.dto.BookmarkRequest;
import org.example.bookmark.dto.BookmarkResponse;
import org.example.bookmark.entity.Bookmark;
import org.example.bookmark.entity.Member;
import org.example.bookmark.entity.Tag;
import org.example.bookmark.repository.BookmarkRepository;
import org.example.bookmark.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookmarkServiceTest {

    @Mock
    BookmarkRepository bookmarkRepository;

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    BookmarkService bookmarkService;

  private static final Long MEMBER_ID = 1L;
  private static final Long OTHER_MEMBER_ID = 2L;
  private static final Long BOOKMARK_ID = 1L;
  private static final Long NON_EXISTING_BOOKMARK_ID = 999L;

    @Test
    @DisplayName("북마크 등록")
    void registerBookmark() {

      // given
      BookmarkRequest request = createBookmarkRequest(false);
      Member member = createMember(MEMBER_ID);

      mockFindMemberById(MEMBER_ID, member);
      mockSaveBookmark();

      // when
      bookmarkService.registerBookmark(MEMBER_ID,request);

      // then
      ArgumentCaptor<Bookmark> captor = ArgumentCaptor.forClass(Bookmark.class);
      verify(bookmarkRepository).save(captor.capture());
      Bookmark savedBookmark = captor.getValue();

      assertEquals("테스트 북마크", savedBookmark.getTitle());
      assertEquals("https://example.com", savedBookmark.getUrl());
      assertEquals("메모 내용", savedBookmark.getMemo());
      assertEquals(2, savedBookmark.getTags().size());
      assertBookmarkTags(savedBookmark,List.of("포털","검색"));
      assertEquals(MEMBER_ID, savedBookmark.getMember().getId());
    }

    @Test
    @DisplayName("북마크 상세 조회 - 정상")
    void getBookmarkById_success() {

      // given
      Member member = createMember(MEMBER_ID);
      Bookmark bookmark = createBookmark(BOOKMARK_ID, member);

      mockFindBookmarkById(BOOKMARK_ID, Optional.of(bookmark));

      // when
      BookmarkResponse response = bookmarkService.getBookmarkById(MEMBER_ID, BOOKMARK_ID);

      // then
      verify(bookmarkRepository).findById(BOOKMARK_ID);

      assertEquals(1L, response.getId());
      assertEquals("테스트 북마크", response.getTitle());
      assertEquals("https://example.com", response.getUrl());
      assertEquals("메모 내용", response.getMemo());
    }

    @Test
    @DisplayName("북마크 상세 조회 - 예외(해당 ID의 북마크가 존재하지 않는 경우)")
    void getBookmarkById_bookmarkNotFound() {

      // given
      mockFindBookmarkById(NON_EXISTING_BOOKMARK_ID, Optional.empty());

      // when & then
      BusinessException exception = assertThrows(BusinessException.class, () -> {
        bookmarkService.getBookmarkById(MEMBER_ID, NON_EXISTING_BOOKMARK_ID);
      });

      // then
      assertEquals(ErrorCode.BOOKMARK_NOT_FOUND, exception.getErrorCode());
      verify(bookmarkRepository).findById(NON_EXISTING_BOOKMARK_ID);
    }

    @Test
    @DisplayName("북마크 상세 조회 - 예외(본인 계정의 북마크가 아닌 경우)")
    void getBookmarkById_unauthorizedAccess() {

      // given
      Member otherMember = createMember(OTHER_MEMBER_ID);
      Bookmark bookmark = createBookmark(BOOKMARK_ID, otherMember);

      mockFindBookmarkById(BOOKMARK_ID, Optional.of(bookmark));

      // when
      BusinessException exception = assertThrows(BusinessException.class, () -> {
        bookmarkService.getBookmarkById(MEMBER_ID, BOOKMARK_ID);
      });

      // then
      assertEquals(ErrorCode.UNAUTHORIZED_ACCESS, exception.getErrorCode());
      verify(bookmarkRepository).findById(BOOKMARK_ID);
    }

  @Test
  @DisplayName("북마크 수정 - 정상")
  void updateBookmark_success() {

    // given
    BookmarkRequest request = createBookmarkRequest(true);
    Member member =  createMember(MEMBER_ID);
    Bookmark existingBookmark = createBookmark(BOOKMARK_ID, member);

    mockFindBookmarkById(BOOKMARK_ID, Optional.of(existingBookmark));
    mockSaveBookmark();

    // when
    BookmarkResponse response = bookmarkService.updateBookmark(MEMBER_ID, BOOKMARK_ID, request);

    // then
    verify(bookmarkRepository).findById(BOOKMARK_ID);
    verify(bookmarkRepository).save(existingBookmark);

    assertEquals("업데이트 북마크", existingBookmark.getTitle());
    assertEquals("https://update.com", existingBookmark.getUrl());
    assertEquals("업데이트 메모", existingBookmark.getMemo());
    assertEquals(2, existingBookmark.getTags().size());
    assertBookmarkTags(existingBookmark,List.of("프로그래밍","뉴스"));
  }

  @Test
  @DisplayName("북마크 수정 - 예외(해당 ID의 북마크가 존재하지 않는 경우)")
  void updateBookmark_bookmarkNotFound() {

    // given
    BookmarkRequest request = createBookmarkRequest(true);

    mockFindBookmarkById(NON_EXISTING_BOOKMARK_ID,Optional.empty());

    // when
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      bookmarkService.updateBookmark(MEMBER_ID ,NON_EXISTING_BOOKMARK_ID, request);
    });

    // then
    assertEquals(ErrorCode.BOOKMARK_NOT_FOUND, exception.getErrorCode());
    verify(bookmarkRepository).findById(NON_EXISTING_BOOKMARK_ID);
    verify(bookmarkRepository, never()).save(any());
  }

  @Test
  @DisplayName("북마크 상세 조회 - 예외(본인 계정의 북마크가 아닌 경우)")
  void updateBookmark_unauthorizedAccess() {

    // given
    Member otherMember = createMember(OTHER_MEMBER_ID);
    Bookmark bookmark = createBookmark(BOOKMARK_ID, otherMember);

    mockFindBookmarkById(BOOKMARK_ID, Optional.of(bookmark));

    // when
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      bookmarkService.getBookmarkById(MEMBER_ID, BOOKMARK_ID);
    });

    // then
    assertEquals(ErrorCode.UNAUTHORIZED_ACCESS, exception.getErrorCode());
    verify(bookmarkRepository).findById(BOOKMARK_ID);
  }


  @Test
  @DisplayName("북마크 삭제 - 정상")
  void deleteBookmark_success() {

    // given
    Member member = createMember(MEMBER_ID);
    Bookmark bookmark =  createBookmark(BOOKMARK_ID, member);

    mockFindBookmarkById(BOOKMARK_ID, Optional.of(bookmark));

    // when
    bookmarkService.deleteBookmark(MEMBER_ID,BOOKMARK_ID);

    // then
    verify(bookmarkRepository).findById(BOOKMARK_ID);
    verify(bookmarkRepository).delete(bookmark);
  }

  @Test
  @DisplayName("북마크 삭제 - 예외(존재하지 않는 경우)")
  void deleteBookmark_bookmarkNotFound() {

    // given
    mockFindBookmarkById(NON_EXISTING_BOOKMARK_ID,Optional.empty());

    // when
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      bookmarkService.deleteBookmark(MEMBER_ID, NON_EXISTING_BOOKMARK_ID);
    });

    // then
    assertEquals(ErrorCode.BOOKMARK_NOT_FOUND, exception.getErrorCode());
    verify(bookmarkRepository).findById(NON_EXISTING_BOOKMARK_ID);
    verify(bookmarkRepository, never()).delete(any());
  }

  @Test
  @DisplayName("북마크 삭제 - 예외(본인 계정의 북마크가 아닌 경우)")
  void deleteBookmark_unauthorizedAccess() {

    // given
    Member otherMember = createMember(OTHER_MEMBER_ID);
    Bookmark bookmark = createBookmark(BOOKMARK_ID, otherMember);

    mockFindBookmarkById(BOOKMARK_ID, Optional.of(bookmark));

    // when
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      bookmarkService.deleteBookmark(MEMBER_ID, BOOKMARK_ID);
    });

    assertEquals(ErrorCode.UNAUTHORIZED_ACCESS, exception.getErrorCode());
    verify(bookmarkRepository).findById(BOOKMARK_ID);
    verify(bookmarkRepository, never()).delete(any());
  }


  // ------------------- private helpers -------------------

  private BookmarkRequest createBookmarkRequest(boolean isUpdate) {

    if(isUpdate) {
      return BookmarkRequest.builder()
          .title("업데이트 북마크")
          .url("https://update.com")
          .memo("업데이트 메모")
          .tags(List.of("뉴스","프로그래밍"))
          .build();
    }

    return BookmarkRequest.builder()
        .title("테스트 북마크")
        .url("https://example.com")
        .memo("메모 내용")
        .tags(List.of("포털", "검색"))
        .build();
  }

  private Bookmark createBookmark(Long bookmarkId, Member member) {
    return Bookmark.builder()
        .id(bookmarkId)
        .title("테스트 북마크")
        .url("https://example.com")
        .memo("메모 내용")
        .member(member)
        .build();
  }

  private Member createMember(Long memberId) {
    return Member.builder()
        .id(memberId)
        .build();
  }

  private void mockFindMemberById(Long memberId, Member member) {
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
  }

  private void mockFindBookmarkById(Long bookmarkId, Optional<Bookmark> returnValue) {
    when(bookmarkRepository.findById(bookmarkId)).thenReturn(returnValue);
  }

  private void mockSaveBookmark() {
    when(bookmarkRepository.save(any(Bookmark.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  private void assertBookmarkTags(Bookmark bookmark, List<String> expectedTagNames) {
    Assertions.assertThat(bookmark.getTags())
        .extracting(Tag::getName)
        .containsExactlyInAnyOrderElementsOf(expectedTagNames);
  }

}
