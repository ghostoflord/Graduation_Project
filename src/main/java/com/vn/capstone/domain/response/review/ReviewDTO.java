package com.vn.capstone.domain.response.review;

import java.time.Instant;

public class ReviewDTO {
    private Long id;
    private Long productId;
    private Long userId;
    private String username;
    private float rating;
    private Instant createdAt;

    public ReviewDTO() {
    }

    public ReviewDTO(Long id, Long productId, Long userId, String username, float rating, String comment,
            Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.username = username;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
