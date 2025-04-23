package com.vn.capstone.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimplifiedCartDetailDTO {
    private Long id;
    private long quantity;
    private double price;
    private Long cartId; // Chỉ lấy ID của Cart
    private Long userId; // Chỉ lấy ID của User
}
