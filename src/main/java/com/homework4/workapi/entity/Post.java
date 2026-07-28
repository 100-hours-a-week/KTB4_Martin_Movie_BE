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

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    private int likeCount;
    private LocalDateTime updateTime;
    private long viewCount;
    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    @BatchSize(size = 20)
    private List<Attach> attaches = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PostView> postViews = new ArrayList<>();

    protected Post() {}

    public Post(User user, String title, String content, int rating) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.createTime = LocalDateTime.now();
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

