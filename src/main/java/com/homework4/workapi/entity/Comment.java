package com.homework4.workapi.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    protected Comment() {
    }

    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "post_id")
            private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "user_id")
            private User user;

    public Comment(User user, Post post, String content) {
        this.user = user;
        this.post = post;
        this.content = content;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public void updateComment(String content) {
        this.updateTime = LocalDateTime.now();
        if(content != null){
            this.content = content;
        }
    }

    public boolean isWritten(Long userId) {
        return this.user.getId().equals(userId);
    }

}


