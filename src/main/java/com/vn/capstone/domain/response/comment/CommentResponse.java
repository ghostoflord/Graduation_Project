package com.vn.capstone.domain.response.comment;

import java.time.LocalDateTime;

public class CommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private SimpleUser user;

    public static class SimpleUser {
        private Long id;
        private String name;
        private String avatar;

        // Constructors
        public SimpleUser(Long id, String name, String avatar) {
            this.id = id;
            this.name = name;
            this.avatar = avatar;
        }

        // Getters and setters
        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getAvatar() {
            return avatar;
        }
    }

    // Constructors
    public CommentResponse(Long id, String content, LocalDateTime createdAt, SimpleUser user) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.user = user;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public SimpleUser getUser() {
        return user;
    }
}