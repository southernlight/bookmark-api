package org.example.bookmark.dto;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.bookmark.common.exception.BusinessException;
import org.example.bookmark.common.exception.ErrorCode;
import org.example.bookmark.entity.QBookmark;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Schema(description = "북마크 조회 조건 필터링")
public class BookmarkCriteria {

  @Schema(description = "태그명으로 필터링", example = "포털")
  private String tag;

  @Schema(description = "제목 키워드로 필터링",example ="Naver")
  private String title;

  @Schema(description = "URL 키워드로 필터링",example = "https://www.naver.com")
  private String url;

  @Schema(description = "페이지 번호 (0부터 시작)", example = "0")
  private int page = 0;

  @Schema(description = "페이지 크기", example = "10")
  private int size = 10;

  @Schema(description = "정렬할 필드명",example = "createdAt")
  private String sort = "createdAt";

  @Schema(description = "정렬 방향 (ASC 또는 DESC)",example = "DESC")
  private String direction = "DESC";


  public BooleanExpression toPredicate(QBookmark bookmark) {

    return tagEquals(tag, bookmark)
        .and(titleContains(title, bookmark))
        .and(urlContains(url, bookmark));
  }

  public OrderSpecifier<?> toOrderSpecifier(QBookmark bookmark) {
    boolean isAsc = "asc".equalsIgnoreCase(direction);

    return switch (sort) {
      case "createdAt" -> isAsc ? bookmark.createdAt.asc() : bookmark.createdAt.desc();
      case "id" -> isAsc ? bookmark.id.asc() : bookmark.id.desc();
      default -> throw new BusinessException(ErrorCode.UNSUPPORTED_SORT_FIELD);
    };
  }

  public Pageable toPageable() {
    return PageRequest.of(page, size);
  }

  private BooleanExpression tagEquals(String tag, QBookmark bookmark) {
    if (tag == null || tag.isEmpty()) {
      return Expressions.asBoolean(true).isTrue();
    }
    return bookmark.tags.any().name.eq(tag);
  }

  private BooleanExpression titleContains(String title, QBookmark bookmark) {
    if (title == null || title.isEmpty()) {
      return Expressions.asBoolean(true).isTrue();
    }
    return bookmark.title.containsIgnoreCase(title);
  }

  private BooleanExpression urlContains(String url, QBookmark bookmark) {
    if (url == null || url.isEmpty()) {
      return Expressions.asBoolean(true).isTrue();
    }
    return bookmark.url.contains(url);
  }

}
