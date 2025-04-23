package com.vn.capstone.domain.response;

public class CommentRequest {
    private Long userId;
    private Long productId;
    private String content;

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CommentRequest{" +
                "userId=" + userId +
                ", productId=" + productId +
                ", content='" + content + '\'' +
                '}';
    }
}