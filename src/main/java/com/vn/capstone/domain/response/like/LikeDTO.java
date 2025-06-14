package com.vn.capstone.domain.response.like;

import java.math.BigDecimal;

public class LikeDTO {
    private Long likeId;
    private Long userId;
    private String userName;
    private Long productId;
    private String productName;
    private String productThumbnail;
    private String productDescription;
    private String productPrice;

    public LikeDTO(Long likeId, Long userId, String userName, Long productId, String productName,
            String productThumbnail, String productDescription, String productPrice) {
        this.likeId = likeId;
        this.userId = userId;
        this.userName = userName;
        this.productId = productId;
        this.productName = productName;
        this.productThumbnail = productThumbnail;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
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

    public String getProductThumbnail() {
        return productThumbnail;
    }

    public void setProductThumbnail(String productThumbnail) {
        this.productThumbnail = productThumbnail;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

}