package com.homework4.workapi.repository;

import com.homework4.workapi.entity.Comment;
import com.homework4.workapi.projection.CommentCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c join fetch c.user where c.post.id = :postId")
    List<Comment> findByPostIdWithUser(@Param("postId") Long postId);

    int countByPost_Id(Long postId);

    @Query("""
    select c.post.id as postId, count(c.id) as commentCount
    from Comment c
    where c.post.id in :postIds
    group by c.post.id
""")
    List<CommentCountProjection> countByPostIds(@Param("postIds") List<Long> postIds);
}
