package org.example.bookmark.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookmark.common.response.ApiResponse;
import org.example.bookmark.common.response.SuccessCode;
import org.example.bookmark.controller.docs.AuthControllerDocs;
import org.example.bookmark.dto.LoginRequest;
import org.example.bookmark.dto.SignUpRequest;
import org.example.bookmark.entity.Member;
import org.example.bookmark.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<Void>> signup(@RequestBody @Valid SignUpRequest request) {

    authService.signup(request.getEmail(), request.getPassword());

    return ApiResponse.of(SuccessCode.OK, null);
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<Void>> login(@RequestBody @Valid LoginRequest request, HttpSession session) {

    Member member = authService.login(request.getEmail(), request.getPassword());
    session.setAttribute("LOGIN_MEMBER", member);

    return ApiResponse.of(SuccessCode.OK, null);
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
    session.removeAttribute("LOGIN_MEMBER");
    session.invalidate();
    return ApiResponse.of(SuccessCode.OK, null);
  }

}
