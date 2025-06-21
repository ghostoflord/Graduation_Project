package com.vn.capstone.domain.response.flashsale;

public class FlashSaleItemUpdateDTO {
    private Long id;
    private Long productId;
    private Double salePrice;
    private Integer quantity;

    public FlashSaleItemUpdateDTO() {
    }

    public FlashSaleItemUpdateDTO(Long id, Long productId, Double salePrice, Integer quantity) {
        this.id = id;
        this.productId = productId;
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
}