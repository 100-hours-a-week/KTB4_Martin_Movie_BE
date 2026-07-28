package com.homework4.workapi.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "attaches", uniqueConstraints = @UniqueConstraint(
        name = "uk_attach_post_upload_key",
        columnNames = {"post_id", "upload_key"}
    )
)
public class Attach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, updatable = false, length = 36)
    private String uploadKey;

    @Column(nullable = false)
    private String attachUrl;

    @Column(nullable = false)
    private LocalDateTime attachTime;

    protected Attach(){}

    public Attach(Post post, String attachUrl, String uploadKey) {
        this.post = post;
        this.attachUrl = attachUrl;
        this.uploadKey = uploadKey;
        this.attachTime = LocalDateTime.now();
    }
}
