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
import com.vn.capstone.domain.response.order.OrderResponse;
import com.vn.capstone.domain.response.order.PlaceOrderRequest;
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

    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}
