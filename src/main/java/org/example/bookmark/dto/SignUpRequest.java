package org.example.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "회원가입 요청 DTO")
public class SignUpRequest {

  @Schema(description = "회원 이메일", example = "example@email.com", required = true)
  @NotBlank(message = "이메일은 필수입니다.")
  private String email;

  @Schema(description = "회원 비밀번호", example = "password123", required = true)
  @NotBlank(message ="비밀번호는 필수입니다.")
  private String password;
}
