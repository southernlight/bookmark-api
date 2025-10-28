package org.example.bookmark.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDSLConfig {

  @Bean
  public JPAQueryFactory jpqQueryFactory(EntityManager em) {
    return new JPAQueryFactory(em);
  }

}
