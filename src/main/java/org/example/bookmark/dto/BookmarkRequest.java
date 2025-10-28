package org.example.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "북마크 요청 DTO")
public class BookmarkRequest {

  @NotBlank(message = "북마크 제목은 필수입니다")
  @Schema(description = "북마크 제목",example = "Naver")
  private String title;

  @NotBlank(message = "북마크 URL은 필수입니다")
  @Schema(description = "북마크 URL", example = "https://www.naver.com")
  private String url;

  @Schema(description = "메모", example = "네이버 포털")
  private String memo;

  @Schema(description = "북마크에 추가할 태그 목록",
      example = "[\"포털\", \"검색\"]")
  private List<String> tags;

}
