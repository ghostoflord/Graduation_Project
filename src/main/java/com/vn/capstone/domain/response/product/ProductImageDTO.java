package com.vn.capstone.domain.response.product;

public class ProductImageDTO {
    private Long id;
    private String imageUrl;

    public ProductImageDTO() {
    }

    public ProductImageDTO(Long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
