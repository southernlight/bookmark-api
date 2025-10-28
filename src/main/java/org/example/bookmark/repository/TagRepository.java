package org.example.bookmark.repository;

import java.util.Optional;
import org.example.bookmark.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
  Optional<Tag> findByName(String name);
}
