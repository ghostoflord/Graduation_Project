package com.vn.capstone.domain.response.flashsale;

public class FlashSaleItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Double originalPrice;
    private Double salePrice;
    private Integer quantity;
    public FlashSaleItemDTO() {
    }

    public FlashSaleItemDTO(Long id, Long productId, String productName, Double originalPrice, Double salePrice,
            Integer quantity) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.originalPrice = originalPrice;
        this.salePrice = salePrice;
        this.quantity = quantity;
    }

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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // Getters và setters
}
