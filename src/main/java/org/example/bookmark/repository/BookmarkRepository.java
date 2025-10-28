package org.example.bookmark.repository;

import java.util.List;
import org.example.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  List<Bookmark> findDistinctByTags_Name(String tagName);
}
