package com.homework4.workapi.repository;

import com.homework4.workapi.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPost_IdAndUser_Id(Long postId, Long userId);
    @Query("""
                select postLike.post.id
                from PostLike postLike
                where postLike.user.id = :userId
                  and postLike.post.id in :postIds
           """)
    List<Long> findLikedPostIds(
            @Param("userId") Long userId,
            @Param("postIds") List<Long> postIds
    );
}