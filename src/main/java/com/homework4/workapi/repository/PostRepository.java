package com.homework4.workapi.repository;

import com.homework4.workapi.entity.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PostRepository extends JpaRepository<Post,Long> {
    @EntityGraph(attributePaths = {"user"})
    @Query("select p from Post p")
    Page<Post> findAllWithUser(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    List<Post> findTop5ByOrderByCreateTimeDesc();
}
