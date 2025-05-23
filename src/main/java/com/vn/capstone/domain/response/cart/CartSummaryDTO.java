package com.vn.capstone.domain.response.cart;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartSummaryDTO {
    private long quantity;
    private double price;
    private long sum;
    private Long userId;
    private List<CartItemDTO> items;
}
