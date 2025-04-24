package com.vn.capstone.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.Order;
import com.vn.capstone.service.OrderService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest req) {

        Order order = orderService.placeOrder(
                req.getUserId(),
                req.getName(), // hoặc req.getName()
                req.getAddress(),
                req.getPhone());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(OrderResponse.from(order));
    }

    public static class PlaceOrderRequest {
        private Long userId;
        private String name;
        private String address;
        private String phone;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    @Getter
    @AllArgsConstructor
    static class OrderResponse {
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

    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}
