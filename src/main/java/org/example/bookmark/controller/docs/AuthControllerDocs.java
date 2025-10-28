package org.example.bookmark.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.example.bookmark.common.response.ApiResponse;
import org.example.bookmark.dto.LoginRequest;
import org.example.bookmark.dto.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface AuthControllerDocs {

  @Operation(
      summary = "회원 가입",
      description = "이메일과 비밀번호로 회원 생성"
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "회원 가입 성공"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "잘못된 요청",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponse.class),
              examples = {
                  @ExampleObject(
                      name = "이메일 중복",
                      description = "이미 가입된 이메일로 회원가입을 시도했을 때 발생하는 에러",
                      value = """
                        {
                          "status": 400,
                          "message": "이미 사용중인 이메일입니다.",
                          "data": null
                        }
                        """),
                  @ExampleObject(
                      name = "필수값 누락",
                      description = "이메일 또는 비밀번호가 누락된 경우 발생하는 검증 에러",
                      value = """
                        {
                          "status": 400,
                          "message": "email: 이메일은 필수입니다., password: 비밀번호는 필수입니다.",
                          "data": null
                        }
                        """)
              }
          )
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          ref = "#/components/responses/InternalServerError"
      )

  })
  ResponseEntity<ApiResponse<Void>> signup(
      @Parameter(description = "회원 가입 요청 DTO", required = true)
      SignUpRequest request
  );

  @Operation(
      summary = "로그인(세션 방식)",
      description = "로그인 성공 시 세션 생성"
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "로그인 성공"
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "잘못된 요청",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponse.class),
              examples = {
                  @ExampleObject(
                      name = "이메일 또는 비밀번호 오류",
                      description = "사용자가 입력한 이메일이 가입된 계정이 아니거나 비밀번호가 일치하지 않을 때 발생하는 에러",
                      value = """
                          {
                            "status": 400,
                            "message": "이메일 또는 비밀번호가 일치하지 않습니다",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "필수값 누락",
                      description = "이메일 또는 비밀번호가 누락된 경우 발생하는 검증 에러",
                      value = """
                          {
                            "status": 400,
                            "message": "email: 이메일은 필수입니다., password: 비밀번호는 필수입니다.",
                            "data": null
                          }
                          """
                  )
              }
          )
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          ref = "#/components/responses/InternalServerError"
      )
  })
  ResponseEntity<ApiResponse<Void>> login(
      @Parameter(description = "로그인 요청 DTO", required = true)
      LoginRequest request, HttpSession session);

  @Operation(
      summary = "로그아웃",
      description = "세션에서 로그인 정보를 제거하여 로그아웃 처리"
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "로그아웃 성공"
      ),

      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "401",
          ref = "#/components/responses/Unauthorized"
      ),

      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "500",
          ref = "#/components/responses/InternalServerError"
      )
  })
  ResponseEntity<ApiResponse<Void>> logout(HttpSession session);

}
