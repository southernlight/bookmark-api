package org.example.bookmark.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

  OK(HttpStatus.OK, "요청이 성공했습니다.");

  private final HttpStatus httpStatus;
  private final String message;

  public int getStatusCode() {
    return httpStatus.value();
  }

}
