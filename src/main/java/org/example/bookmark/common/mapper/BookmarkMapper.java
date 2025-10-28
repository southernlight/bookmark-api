package org.example.bookmark.common.mapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.example.bookmark.dto.BookmarkRequest;
import org.example.bookmark.dto.BookmarkResponse;
import org.example.bookmark.entity.Bookmark;
import org.example.bookmark.entity.Tag;

public class BookmarkMapper {
  private BookmarkMapper() {}

  public static BookmarkResponse toBookmarkResponse(Bookmark bookmark) {
    return BookmarkResponse.builder()
        .id(bookmark.getId())
        .title(bookmark.getTitle())
        .url(bookmark.getUrl())
        .memo(bookmark.getMemo())
        .createdAt(bookmark.getCreatedAt())
        .updatedAt(bookmark.getUpdatedAt())
        .build();
  }

  public static Bookmark toBookmarkEntity(BookmarkRequest request) {

    Bookmark bookmark = Bookmark.builder()
        .title(request.getTitle())
        .url(request.getUrl())
        .memo(request.getMemo())
        .build();

    if(request.getTags() != null &&  !request.getTags().isEmpty()) {
      Set<Tag> tags = request.getTags().stream()
          .map(tagName -> Tag.builder().name(tagName).build())
          .collect(Collectors.toSet());
      bookmark.setTags(tags);
    }
    return bookmark;
  }

  public static Set<Tag> toTagSet(List<String> tagList) {
    return Optional.ofNullable(tagList)
        .orElse(List.of())
        .stream()
        .map(name -> Tag.builder().name(name).build())
        .collect(Collectors.toSet());
  }

  public static List<BookmarkResponse> toBookmarkResponseList(List<Bookmark> bookmarks) {
    return bookmarks.stream()
        .map(BookmarkMapper::toBookmarkResponse)
        .toList();
  }
}
