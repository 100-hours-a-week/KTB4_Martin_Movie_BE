package com.homework4.workapi.repository;

import com.homework4.workapi.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PostRepository extends JpaRepository<Post,Long> {
    @EntityGraph(attributePaths = {"user"})
    @Query("select p from Post p")
    Page<Post> findAllWithUser(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    List<Post> findTop5ByOrderByCreateTimeDesc();

    @EntityGraph(attributePaths = {"user", "attaches"})
    @Query("""
        select distinct p
        from Post p
        where p.id in :postIds
        """)
    List<Post> findAllByIdsWithDetails(@Param("postIds") List<Long> post);

}
