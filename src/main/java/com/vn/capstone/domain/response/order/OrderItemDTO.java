package com.vn.capstone.domain.response.order;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

public class OrderItemDTO {
    private Long productId;
    private String productName;
    private String productImage;
    private long quantity;
    private double price;
}