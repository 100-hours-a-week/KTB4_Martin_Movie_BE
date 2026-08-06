package com.homework4.workapi.repository;

import com.homework4.workapi.entity.Post;
import com.homework4.workapi.projection.PostListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PostRepository extends JpaRepository<Post,Long> {
    @EntityGraph(attributePaths = {"user"})
    List<Post> findTop5ByOrderByCreateTimeDesc();

    @Query("""
        select
            p.id as id,
            p.title as title,
            p.content as content,
            u.username as username,
            u.deleted as deleted,
            u.profileImageUrl as profileImageUrl,
            p.likeCount as likeCount,
            p.createTime as createTime,
            p.viewCount as viewCount,
            p.rating as rating
        from Post p
        join p.user u
        where p.id in :postIds
        """)
    List<PostListProjection> findAllPostListByIds(
            @Param("postIds") List<Long> postIds
    );

    @Query(
            value = """
                select
                    p.id as id,
                    p.title as title,
                    p.content as content,
                    u.username as username,
                    u.deleted as deleted,
                    u.profileImageUrl as profileImageUrl,
                    p.likeCount as likeCount,
                    p.createTime as createTime,
                    p.viewCount as viewCount,
                    p.rating as rating
                from Post p
                join p.user u
                """,
            countQuery = """
                select count(p)
                from Post p
                """
    )
    Page<PostListProjection> findPostList(Pageable pageable);

}
