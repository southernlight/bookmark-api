package org.example.bookmark.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.bookmark.common.response.ApiResponse;
import org.example.bookmark.dto.BookmarkCriteria;
import org.example.bookmark.dto.BookmarkRequest;
import org.example.bookmark.dto.BookmarkResponse;
import org.example.bookmark.dto.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Bookmark", description = "북마크 관련 API - 로그인 필요")
public interface BookmarkControllerDocs {

  @Operation(
      summary = "북마크 등록(태그도 등록)",
      description = """
        북마크를 추가합니다.<br><br>
        
        ⚡ <b>주의 사항</b><br>
        - 북마크 추가시 tags 필드에 태그 문자열 리스트를 함께 전달할 수 있습니다.<br><br>
        """)
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "북마크 등록 성공"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "401",
          ref = "#/components/responses/Unauthorized"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          ref = "#/components/responses/MemberNotFound"
      ),
  })
  ResponseEntity<ApiResponse<BookmarkResponse>> registerBookmark(
      @RequestAttribute("LOGIN_MEMBER_ID") Long memberId,
      @RequestBody BookmarkRequest request);

  @Operation(
      summary = "북마크 목록 조회 (검색, 페이지네이션, 태그별 조회)",
      description = """
        사용자의 북마크를 조건에 맞게 검색하고, 페이지네이션 형태로 조회합니다.<br><br>
        🔍 <b>검색 조건 설명</b><br>
        - <b>tag</b> : 북마크가 가진 태그 중 하나라도 tag 값과 <b>정확히 일치</b>하면 포함됩니다.<br>
        - <b>title</b> : 제목에 해당 문자열이 <b>부분 포함</b>되면 매칭됩니다. (대소문자 무시)<br>
        - <b>url</b> : URL에 해당 문자열이 <b>부분 포함</b>되면 매칭됩니다.<br>
        - <b>page</b> : 조회할 페이지 번호 (0부터 시작).<br>
        - <b>size</b> : 한 페이지에 포함될 항목 수.<br>
        - <b>sort</b> : 정렬 기준 필드명 (예: createdAt).<br>
        - <b>direction</b> : 정렬 방향 (ASC 또는 DESC).<br><br>
        """
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "북마크 목록 조회 성공"
      ),

      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          ref = "#/components/responses/InternalServerError"
      )
  })
  ResponseEntity<ApiResponse<PageResponse<BookmarkResponse>>> getBookmarksPage(
      @RequestAttribute("LOGIN_MEMBER_ID") Long memberId,
      @Parameter(description = "검색 필터 (tag, title, url)")
      @ModelAttribute BookmarkCriteria criteria
  );

  @Operation(
      summary = "북마크 상세 조회",
      description = """
        북마크 ID로 특정 북마크를 조회합니다.<br><br>
        
        ⚡ <b>주의 사항</b><br>
        - 북마크 조회는 본인 계정의 북마크만 가능합니다.<br><br>
        """)
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "북마크 상세 조회 성공"
      ),

      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          ref = "#/components/responses/BookmarkNotFound"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "401",
          ref = "#/components/responses/BookmarkForbidden"
      ),

      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          ref = "#/components/responses/InternalServerError"
      )
  })
  ResponseEntity<ApiResponse<BookmarkResponse>> getBookmarkById(
      @RequestAttribute("LOGIN_MEMBER_ID") Long memberId,
      @Parameter(description = "조회할 북마크 ID", required = true)
      @PathVariable("id") Long bookmarkId);

  @Operation(
      summary = "북마크 수정",
      description = """
        북마크 ID로 특정 북마크를 갱신합니다.<br><br>
        
        ⚡ <b>주의 사항</b><br>
        - 북마크 title과 url은 필수값입니다. 나머지 값을 넣지 않으면, 필드가 null로 변경 됩니다.<br>
        - 북마크 갱신은 본인 계정의 북마크만 가능합니다.<br><br>
        """)
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "북마크 수정 성공"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "북마크 ID 없음 또는 필수값 누락",
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "북마크 존재하지 않음",
                      description = "해당 ID의 북마크를 찾을 수 없는 경우",
                      value = """
                        {
                          "status": 400,
                          "message": "해당 ID의 북마크를 찾을 수 없습니다.",
                          "data": null
                        }
                        """
                  ),
                  @ExampleObject(
                      name = "필수값 누락",
                      description = "title 또는 url이 누락된 경우 발생하는 검증 에러",
                      value = """
                        {
                          "status": 400,
                          "message": "title: 북마크 제목은 필수입니다., url: 북마크 URL은 필수입니다.",
                          "data": null
                        }
                        """
                  )
              }
          )
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "401",
          ref = "#/components/responses/BookmarkForbidden"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          ref = "#/components/responses/InternalServerError"
      )
  })
  ResponseEntity<ApiResponse<BookmarkResponse>> updateBookmarkById(
      @RequestAttribute("LOGIN_MEMBER_ID") Long memberId,
      @Parameter(description = "수정할 북마크 ID", required = true)
      @PathVariable("id") Long bookmarkId,
      @RequestBody BookmarkRequest request);

  @Operation(
      summary = "북마크 삭제",
      description = """
        북마크 ID로 특정 북마크를 삭제합니다.<br><br>
        
        ⚡ <b>주의 사항</b><br>
        - 북마크 삭제는 본인 계정의 북마크만 가능합니다.<br><br>
        """)
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "북마크 삭제 성공"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          ref = "#/components/responses/BookmarkNotFound"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "401",
          ref = "#/components/responses/BookmarkForbidden"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          ref = "#/components/responses/InternalServerError"
      )
  })
  ResponseEntity<ApiResponse<Void>> deleteBookmarkById(
      @RequestAttribute("LOGIN_MEMBER_ID") Long memberId,
      @Parameter(description = "삭제할 북마크 ID", required = true)
      @PathVariable("id") Long bookmarkId);
}

