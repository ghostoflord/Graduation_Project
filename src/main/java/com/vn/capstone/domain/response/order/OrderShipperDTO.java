package com.vn.capstone.domain.response.order;

import java.time.Instant;
import java.util.List;

import com.vn.capstone.util.constant.OrderStatus;
import com.vn.capstone.util.constant.PaymentMethod;
import com.vn.capstone.util.constant.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderShipperDTO {
    private Long id;

    private double totalPrice;

    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;

    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;

    private String paymentRef;
    private String shippingMethod;
    private String trackingCode;

    private Instant estimatedDeliveryTime;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deliveredAt;

    private String cancelReason;

    // Optional: để biết thông tin người mua
    private String customerName;
    private String customerEmail;
}
