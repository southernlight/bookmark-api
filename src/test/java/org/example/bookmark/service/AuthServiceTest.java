package org.example.bookmark.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.bookmark.common.exception.BusinessException;
import org.example.bookmark.common.exception.ErrorCode;
import org.example.bookmark.entity.Member;
import org.example.bookmark.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_PASSWORD = "password";
  private static final String WRONG_PASSWORD = "wrong";

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private AuthService authService;

  @Test
  @DisplayName("회원가입 - 정상")
  void signup_success() {

    // given
    mockExistsByEmail(TEST_EMAIL,false);

    // when
    authService.signup(TEST_EMAIL, TEST_PASSWORD);

    // then
    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
    verify(memberRepository).save(captor.capture());
    Member savedMember = captor.getValue();

    assertEquals(TEST_EMAIL,savedMember.getEmail());
    assertEquals(TEST_PASSWORD,savedMember.getPassword());
  }

  @Test
  @DisplayName("회원가입 - 예외(해당 Email이 이미 존재하는 경우)")
  void signup_emailExists() {

    // given
    mockExistsByEmail(TEST_EMAIL,true);

    // when
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      authService.signup(TEST_EMAIL, TEST_PASSWORD);
    });

    // then
    assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
  }

  @Test
  @DisplayName("로그인 - 정상")
  void login_success() {

    // given
    Member member = Member.builder()
            .email(TEST_EMAIL)
            .password(TEST_PASSWORD)
            .build();
    mockFindMemberByEmail(TEST_EMAIL,Optional.of(member));

    Member result = authService.login(TEST_EMAIL, TEST_PASSWORD);

    assertEquals(member, result);
  }

  @Test
  @DisplayName("로그인 실패 - 이메일 없음")
  void login_invalidEmail() {

    // given
    mockFindMemberByEmail(TEST_EMAIL, Optional.empty());

    // when
    BusinessException exception = assertThrows(BusinessException.class, () ->
        authService.login(TEST_EMAIL, TEST_PASSWORD)
    );

    // then
    assertEquals(ErrorCode.INVALID_EMAIL_OR_PASSWORD, exception.getErrorCode());
  }

  @Test
  @DisplayName("로그인 실패 - 비밀번호 불일치")
  void login_invalidPassword() {

    // given
    Member member = Member.builder()
        .email(TEST_EMAIL)
        .password("correct")
        .build();

    mockFindMemberByEmail(TEST_EMAIL,Optional.of(member));

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      authService.login(TEST_EMAIL, WRONG_PASSWORD);
    });

    assertEquals(ErrorCode.INVALID_EMAIL_OR_PASSWORD, exception.getErrorCode());
  }

  // ---------------- private helper ----------------

  private void mockExistsByEmail(String email,boolean returnValue) {
    when(memberRepository.existsByEmail(email)).thenReturn(returnValue);
  }

  private void mockFindMemberByEmail(String email, Optional<Member> memberOpt) {
    when(memberRepository.findByEmail(email)).thenReturn(memberOpt);
  }

}
