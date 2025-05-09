package com.vn.capstone.domain.response.order;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    // đại diện từng sp trong đơn hàng
    private long productId;
    private String productName;
    private String productImage;
    private long quantity;
    private double price;
}