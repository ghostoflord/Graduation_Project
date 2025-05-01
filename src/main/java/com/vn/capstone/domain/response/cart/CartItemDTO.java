package com.vn.capstone.domain.response.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long productId;
    private String name;
    private double price;
    private long quantity;
    private String image;
    private String detailDescription;
    private String shortDescription;
}
