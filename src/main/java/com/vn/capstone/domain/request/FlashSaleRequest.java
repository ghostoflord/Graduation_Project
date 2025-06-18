package com.vn.capstone.domain.request;

import java.time.LocalDateTime;
import java.util.List;

public class FlashSaleRequest {
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<FlashSaleItemDTO> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<FlashSaleItemDTO> getItems() {
        return items;
    }

    public void setItems(List<FlashSaleItemDTO> items) {
        this.items = items;
    }

    public static class FlashSaleItemDTO {
        private Long productId;
        private Double salePrice;
        private Integer quantity;

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
}
