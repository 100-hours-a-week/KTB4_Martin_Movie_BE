package com.homework4.workapi.repository;

import com.homework4.workapi.entity.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PostRepository extends JpaRepository<Post,Long> {
    @Query("select distinct p from Post p left join fetch p.user left join fetch p.attaches")
    List<Post> findAllWithUserAndAttaches();

    @EntityGraph(attributePaths = {"user"})
    List<Post> findTop5ByOrderByCreateTimeDesc();
}
