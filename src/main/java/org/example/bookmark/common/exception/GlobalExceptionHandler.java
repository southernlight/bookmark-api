package org.example.bookmark.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.bookmark.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<String>> handleBusinessException(BusinessException e) {

    log.error("[Business Exception] {}", e.getMessage());
    return ApiResponse.of(e.getErrorCode());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

    String errorMessage = extractValidationErrors(e);
    log.error("[DTO Validation Failed] {}",errorMessage);
    return ApiResponse.of(ErrorCode.INVALID_REQUEST,errorMessage);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    log.error("[HTTPMessage Not Readable] {}",e.getMessage());
    return ApiResponse.of(ErrorCode.INVALID_REQUEST , e.getMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
    log.error("[Type Mismatch] {}",e.getMessage());
    return ApiResponse.of(ErrorCode.INVALID_REQUEST , e.getMessage());
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ApiResponse<String>> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
    log.error("[Unsupported Media Type]: {}", e.getMessage());
    return ApiResponse.of(ErrorCode.UNSUPPORTED_MEDIA_TYPE, e.getMessage());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiResponse<String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
    log.error("[Method Not Supported]: {}", e.getMessage());
    return ApiResponse.of(ErrorCode.METHOD_NOT_ALLOWED, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
    log.error("[Unhandled Exception]", e);
    return ApiResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
  }

  private String extractValidationErrors(MethodArgumentNotValidException e) {
    return e.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .reduce((msg1, msg2) -> msg1 + ", " + msg2)
        .orElse("Invalid request");
  }
}
