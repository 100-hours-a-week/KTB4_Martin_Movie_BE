package com.homework4.workapi.repository;

import com.homework4.workapi.entity.Attach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttachRepository extends JpaRepository<Attach, Long> {
    List<Attach> findByPost_Id(Long postId);
    Optional<Attach> findByPost_IdAndUploadKey(Long postId, String uploadKey);
}
