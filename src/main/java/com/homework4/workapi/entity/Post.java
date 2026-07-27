package com.homework4.workapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int likeCount;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private long viewCount;
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    @BatchSize(size = 20)
    private List<Attach> attaches = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    protected Post() {}

    public Post(User user, String title, String content, int rating) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.createTime = LocalDateTime.now();
        this.likeCount = 0;
        this.viewCount = 0;
        this.updateTime = LocalDateTime.now();
        this.rating = rating;
    }

    public void viewCountIncrease(){
        this.viewCount += 1;
    }
    public void likeIncrease() {
        this.likeCount++;
    }

    public void likeDecrease() {
        if(this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public boolean isWritten(Long userId) {
        return this.user.getId().equals(userId);
    }

    public void update(String title, String content, int rating) {
        this.updateTime = LocalDateTime.now();
        this.rating = rating;

        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }

    }

}

