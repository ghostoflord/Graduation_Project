package com.vn.capstone.domain.response.like;

public class LikeDTO {
    private Long likeId;
    private Long userId;
    private String userName;
    private Long productId;
    private String productName;

    public LikeDTO(Long likeId, Long userId, String userName, Long productId, String productName) {
        this.likeId = likeId;
        this.userId = userId;
        this.userName = userName;
        this.productId = productId;
        this.productName = productName;
    }

    public Long getLikeId() {
        return likeId;
    }

    public void setLikeId(Long likeId) {
        this.likeId = likeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

}