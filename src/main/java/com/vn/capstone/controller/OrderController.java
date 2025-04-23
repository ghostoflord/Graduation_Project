package com.vn.capstone.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.Order;
import com.vn.capstone.service.OrderService;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public Order placeOrder(@RequestParam Long userId,
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String phone) {
        return orderService.placeOrder(userId, name, address, phone);
    }

    @PostMapping("/place-json")
    public Order placeOrderJson(@RequestBody PlaceOrderRequest request) {
        return orderService.placeOrder(
                request.getUserId(),
                request.getName(),
                request.getAddress(),
                request.getPhone());
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

    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}
