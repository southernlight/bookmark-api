package org.example.bookmark.common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.example.bookmark.entity.Member;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    HttpSession session = request.getSession(false);

    if (session == null) {
      unauthorized(response);
      return false;
    }

    Member member = (Member) session.getAttribute("LOGIN_MEMBER");
    if (member == null) {
      unauthorized(response);
      return false;
    }

    request.setAttribute("LOGIN_MEMBER_ID", member.getId());

    return true;
  }

  private void unauthorized(HttpServletResponse response) throws Exception {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");

    String json = """
        {
          "status": 401,
          "message": "로그인이 필요합니다",
          "data": null
        }
        """;

    response.getWriter().write(json);
  }
}
