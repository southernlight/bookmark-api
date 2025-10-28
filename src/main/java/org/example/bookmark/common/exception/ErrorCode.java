package org.example.bookmark.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다"),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "C002", "요청 데이터가 유효하지 않습니다"),

  BOOKMARK_NOT_FOUND(HttpStatus.BAD_REQUEST, "B001", "해당 ID의 북마크를 찾을 수 없습니다"),
  UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "B002", "본인 계정의 북마크가 아닙니다"),

  EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"A001" ,"이미 사용중인 이메일입니다."),
  INVALID_EMAIL_OR_PASSWORD(HttpStatus.BAD_REQUEST,"A002", "이메일 또는 비밀번호가 일치하지 않습니다"),

  MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST,"M001","멤버를 찾을 수 없습니다");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  public int getStatusCode() {
    return httpStatus.value();
  }
}
