package org.example.bookmark.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.bookmark.common.exception.ErrorCode;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@Getter
public class ApiResponse<T> {

  @Schema(description = "응답 상태 코드", example = "200")
  private final int status;

  @Schema(description = "응답 메시지", example = "요청이 성공했습니다")
  private final String message;

  @Schema(description = "응답 데이터", nullable = true)
  private final T data;

  public static <T> ResponseEntity<ApiResponse<T>> of(SuccessCode successCode, T data) {
    return ResponseEntity
        .status(successCode.getHttpStatus())
        .body(new ApiResponse<>(successCode.getStatusCode(), successCode.getMessage(), data));
  }

  public static <T> ResponseEntity<ApiResponse<T>> of(ErrorCode errorCode, T data) {
    return ResponseEntity
        .status(errorCode.getStatusCode())
        .body(new ApiResponse<>(errorCode.getStatusCode(), errorCode.getMessage(), data));
  }


  public static <T> ResponseEntity<ApiResponse<T>> of(ErrorCode errorCode) {
    return ResponseEntity
        .status(errorCode.getStatusCode())
        .body(new ApiResponse<>(errorCode.getStatusCode(), errorCode.getMessage(), null));
  }

}