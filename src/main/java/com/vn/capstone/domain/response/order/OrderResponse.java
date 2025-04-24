package com.vn.capstone.domain.response.order;

import java.time.Instant;

import com.vn.capstone.domain.Order;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private Double total;
    private String status;
    private Instant createdAt;

    public static OrderResponse from(Order o) {
        return new OrderResponse(
                o.getId(),
                o.getUser().getId(),
                o.getTotalPrice(),
                o.getStatus(),
                o.getCreatedAt());
    }
}
