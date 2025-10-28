package org.example.bookmark.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {

    Components components = new Components();

    // 500 Internal Server Error
    components.addResponses("InternalServerError", createApiResponse(
        500, "서버 내부 오류","서버 내부 오류가 발생했습니다"));

    // 401 Unauthorized
    components.addResponses("Unauthorized", createApiResponse(
        401,"인증 필요" ,"로그인이 필요합니다"));

    // 400 Not Found (북마크 ID 존재하지 않음)
    components.addResponses("BookmarkNotFound", createApiResponse(
        400,"북마크가 존재하지 않음" ,"해당 ID의 북마크를 찾을 수 없습니다"));

    components.addResponses("MemberNotFound", createApiResponse(
        400, "멤버가 존재하지 않음", "멤버를 찾을 수 없습니다"
    ));

    // 401 Forbidden (본인 계정 북마크 아님)
    components.addResponses("BookmarkForbidden", createApiResponse(
        401,"본인 계정의 북마크가 아님" ,"본인 계정의 북마크가 아닙니다"));

    return new OpenAPI()
        .components(components);
  }

  private ApiResponse createApiResponse(int status,String description ,String message) {
    Map<String, Object> example = new LinkedHashMap<>();
    example.put("status", status);
    example.put("message", message);
    example.put("data", null);

    return new ApiResponse()
        .description(description)
        .content(new Content()
            .addMediaType("application/json",
                new MediaType().example(example)
            )
        );
  }
}
