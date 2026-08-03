package com.homework4.workapi.repository;

import com.homework4.workapi.entity.Comment;
import com.homework4.workapi.projection.CommentCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {"user"})
    Page<Comment> findByPost_Id(Long postId, Pageable pageable);

    @Query("""
    select c.post.id as postId, count(c.id) as commentCount
    from Comment c
    where c.post.id in :postIds
    group by c.post.id
""")
    List<CommentCountProjection> countByPostIds(@Param("postIds") List<Long> postIds);

    int countByPost_Id(Long postId);
}
