package com.homework4.workapi.repository;

import com.homework4.workapi.entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    Optional<PostView> findByPost_IdAndUser_Id(Long postId, Long userId);
}
