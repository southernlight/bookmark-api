package org.example.bookmark.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
  private final AuthInterceptor authInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor)
        .excludePathPatterns(
            "/auth/login",            // 로그인 제외
            "/auth/signup",           // 회원가입 제외
            "/swagger-ui/**",         // Swagger UI 제외
            "/v3/api-docs/**",        // OpenAPI docs 제외
            "/swagger-resources/**",  // Swagger 리소스 제외
            "/webjars/**"             // Swagger Webjars 제외
        );
  }
}
