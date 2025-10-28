package org.example.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "북마크 응답 DTO")
public class BookmarkResponse {

  @Schema(description = "북마크 ID", example = "1")
  private Long id;

  @Schema(description = "북마크 제목", example = "Naver")
  private String title;

  @Schema(description = "북마크 URL", example = "https://www.naver.com")
  private String url;

  @Schema(description = "메모", example = "네이버 포털")
  private String memo;

  @Schema(description = "생성 시각", example = "2025-10-27T18:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "수정 시각", example = "2025-10-27T18:05:00")
  private LocalDateTime updatedAt;

  @Schema(description = "북마크에 추가된 태그 목록", example = "[\"포털\", \"검색\"]")
  private List<String> tags;

}
