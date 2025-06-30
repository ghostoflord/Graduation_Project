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

        // Tổng tiền từ tất cả các đơn hàng (có thể lọc theo trạng thái nếu muốn)
        Double totalRevenue = orderRepository.sumTotalPrice();

        // Tổng số lượng sản phẩm bị huỷ (status = CANCELED)
        Long totalCanceledQuantity = orderRepository.sumCanceledOrderQuantity();

        Map<String, Object> data = new HashMap<>();
        data.put("countUser", countUser);
        data.put("countProduct", countProduct);
        data.put("countOrder", countOrder);
        data.put("totalRevenue", totalRevenue);
        data.put("totalCanceledQuantity", totalCanceledQuantity);

        RestResponse<Map<String, Object>> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Success");
        response.setData(data);

        return ResponseEntity.ok(response);
    }
}
