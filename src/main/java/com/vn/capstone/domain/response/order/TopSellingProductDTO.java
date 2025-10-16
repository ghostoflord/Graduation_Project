package com.vn.capstone.domain.response.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopSellingProductDTO {
    private String productName;
    private Long totalQuantity;
    private Double totalRevenue;
}