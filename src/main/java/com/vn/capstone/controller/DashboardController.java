package com.vn.capstone.controller;

import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.repository.OrderRepository;
import com.vn.capstone.repository.ProductRepository;
import com.vn.capstone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DashboardController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public DashboardController(UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData() {
        long countUser = userRepository.count();
        long countProduct = productRepository.count();
        long countOrder = orderRepository.count();

        Map<String, Object> data = new HashMap<>();
        data.put("countUser", countUser);
        data.put("countProduct", countProduct); // Có thể đổi thành "countProduct" nếu muốn rõ ràng hơn
        data.put("countOrder", countOrder);

        RestResponse<Map<String, Object>> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Success");
        response.setData(data);

        return ResponseEntity.ok(response);
    }
}
