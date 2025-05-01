package com.vn.capstone.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.order.OrderResponse;
import com.vn.capstone.domain.response.order.OrderSummaryDTO;
import com.vn.capstone.domain.response.order.PlaceOrderRequest;
import com.vn.capstone.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public ResponseEntity<RestResponse<OrderResponse>> placeOrder(@Valid @RequestBody PlaceOrderRequest req) {
        Order order = orderService.placeOrder(
                req.getUserId(),
                req.getName(),
                req.getAddress(),
                req.getPhone());

        OrderResponse orderResponse = OrderResponse.from(order);

        RestResponse<OrderResponse> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.CREATED.value());
        restResponse.setMessage("Đặt hàng thành công");
        restResponse.setData(orderResponse);
        restResponse.setError(null);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(restResponse);
    }

    @GetMapping("/all")
    public List<OrderSummaryDTO> getAllOrders() {
        return orderService.getAllOrderSummaries();
    }
}
