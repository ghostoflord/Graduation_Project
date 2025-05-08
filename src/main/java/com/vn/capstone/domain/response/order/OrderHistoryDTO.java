package com.vn.capstone.domain.response.order;

import java.time.Instant;
import java.util.List;

import com.vn.capstone.domain.Order;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

public class OrderHistoryDTO {
    private Long orderId;
    private double totalPrice;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private String trackingCode;
    private Instant createdAt;
    private Instant deliveredAt;
    private List<OrderItemDTO> items;
}
