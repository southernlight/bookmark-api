package org.example.bookmark.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.bookmark.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<String>> handleBusinessException(BusinessException e) {

    log.error("비지니스 예외: {}", e.getMessage());
    return ApiResponse.of(e.getErrorCode());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

    String errorMessage = extractValidationErrors(e);
    log.error("DTO 검증 실패: {}",errorMessage);
    return ApiResponse.of(ErrorCode.INVALID_REQUEST,errorMessage);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
    log.error("처리되지 않은 예외", e);
    return ApiResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
  }

  private String extractValidationErrors(MethodArgumentNotValidException e) {
    return e.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .reduce((msg1, msg2) -> msg1 + ", " + msg2)
        .orElse("Invalid request");
  }
}
