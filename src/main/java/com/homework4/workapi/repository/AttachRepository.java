package com.homework4.workapi.repository;

import com.homework4.workapi.entity.Attach;
import com.homework4.workapi.projection.AttachThumbnailProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttachRepository extends JpaRepository<Attach, Long> {
    List<Attach> findByPost_Id(Long postId);

    Optional<Attach> findByPost_IdAndUploadKey(Long postId, String uploadKey);

    @Query("""
        select
            a.post.id as postId,
            a.attachUrl as thumbnailUrl
        from Attach a
        where a.post.id in :postIds
          and a.id in (
              select min(a2.id)
              from Attach a2
              where a2.post.id in :postIds
              group by a2.post.id
          )
        """)
    List<AttachThumbnailProjection> findThumbnailUrlsByPostIds(@Param("postIds") List<Long> postIds);
}
