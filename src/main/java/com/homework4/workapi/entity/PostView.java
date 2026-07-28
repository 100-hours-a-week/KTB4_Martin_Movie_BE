package com.homework4.workapi.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Entity
@Table(name = "post_views", uniqueConstraints = @UniqueConstraint(
                name = "uk_post_views_post_user", columnNames = {"post_id", "user_id"}
        )
)
public class PostView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate viewAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected PostView() {}

    public PostView(Post post, User user, LocalDate viewAt) {
        this.post = post;
        this.user = user;
        this.viewAt = viewAt;
    }

    public boolean hasViewedOn(LocalDate date)
    {
        return viewAt.equals(date);
    }

    public void updateViewAt(LocalDate date) {
        this.viewAt = date;
    }
}
