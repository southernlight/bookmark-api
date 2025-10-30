package org.example.bookmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BookmarkApplication {

  public static void main(String[] args) {
    SpringApplication.run(BookmarkApplication.class, args);
  }

}
