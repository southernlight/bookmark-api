package org.example.bookmark.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookmark.common.response.ApiResponse;
import org.example.bookmark.common.response.SuccessCode;
import org.example.bookmark.controller.docs.BookmarkControllerDocs;
import org.example.bookmark.dto.BookmarkCriteria;
import org.example.bookmark.dto.BookmarkRequest;
import org.example.bookmark.dto.BookmarkResponse;
import org.example.bookmark.dto.PageResponse;
import org.example.bookmark.service.BookmarkService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController implements BookmarkControllerDocs {

  private final BookmarkService bookmarkService;

  @PostMapping
  public ResponseEntity<ApiResponse<BookmarkResponse>> registerBookmark(
      @RequestAttribute("LOGIN_MEMBER_ID") Long memberId , @RequestBody BookmarkRequest request) {

    BookmarkResponse response = bookmarkService.registerBookmark(memberId,request);
    return ApiResponse.of(SuccessCode.OK, response);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<BookmarkResponse>>> getBookmarksPage(
      @RequestAttribute("LOGIN_MEMBER_ID") Long memberId,
      @ModelAttribute BookmarkCriteria criteria) {

    Page<BookmarkResponse> page = bookmarkService.getBookmarksPage(memberId,criteria);
    return ApiResponse.of(SuccessCode.OK, PageResponse.from(page));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<BookmarkResponse>> getBookmarkById(
      @RequestAttribute("LOGIN_MEMBER_ID") Long memberId,
      @PathVariable("id") Long bookmarkId) {

    BookmarkResponse response = bookmarkService.getBookmarkById(memberId,bookmarkId);
    return ApiResponse.of(SuccessCode.OK, response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<BookmarkResponse>> updateBookmarkById(
      @RequestAttribute("LOGIN_MEMBER_ID") Long memberId,
      @PathVariable("id") Long bookmarkId,
      @RequestBody @Valid BookmarkRequest request) {

    BookmarkResponse response = bookmarkService.updateBookmark(memberId, bookmarkId, request);
    return ApiResponse.of(SuccessCode.OK, response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteBookmarkById(
      @RequestAttribute("LOGIN_MEMBER_ID") Long memberId,
      @PathVariable("id") Long bookmarkId) {

    bookmarkService.deleteBookmark(memberId,bookmarkId);
    return ApiResponse.of(SuccessCode.OK, null);
  }
}
