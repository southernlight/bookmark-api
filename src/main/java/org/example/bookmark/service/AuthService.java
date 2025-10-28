package org.example.bookmark.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.bookmark.common.exception.BusinessException;
import org.example.bookmark.common.exception.ErrorCode;
import org.example.bookmark.entity.Member;
import org.example.bookmark.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final MemberRepository memberRepository;

  @Transactional
  public void signup(String email, String password) {

    validateEmailNotExists(email);

    Member member = Member.builder()
        .email(email)
        .password(password)
        .build();

    memberRepository.save(member);
  }

  @Transactional
  public Member login(String email, String password) {

    Member member = findMemberByEmail(email);

    validatePassword(member, password);

    return member;
  }

  // ------------------- private helpers -------------------
  private Member findMemberByEmail(String email) {
    return memberRepository.findByEmail(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_EMAIL_OR_PASSWORD));
  }

  private void validateEmailNotExists(String email) {
    if(memberRepository.existsByEmail(email)) {
      throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }
  }

  private void validatePassword(Member member, String password) {
    if (!member.getPassword().equals(password)) {
      throw new BusinessException(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
    }
  }

}
