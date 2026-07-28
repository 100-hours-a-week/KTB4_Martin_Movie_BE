package com.homework4.workapi.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,  nullable = false, length = 10)
    private String username;

    @Column(unique = true,  nullable = false, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean deleted = false;
    private LocalDateTime deletedAt;
    private String profileImageUrl;

    protected User(){
    }

    public User(String username, String email, String password, String profileImageUrl) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateUser(String username, String email, String password) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void removeProfileImage() {
        this.profileImageUrl = null;
    }

    public void softDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public String getDisplayUsername() {
        return deleted ? "(알 수 없음)" : username;
    }
    public String getDisplayProfileImageUrl(){
        return deleted ? null : profileImageUrl;
    }
}
