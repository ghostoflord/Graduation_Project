package com.vn.capstone.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.order.OrderHistoryDTO;
import com.vn.capstone.domain.response.order.OrderItemDTO;
import com.vn.capstone.domain.response.order.OrderResponse;
import com.vn.capstone.domain.response.order.OrderStatusHistoryDTO;
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
        RestResponse<OrderResponse> restResponse = new RestResponse<>();

        try {
            Order order = orderService.placeOrder(
                    req.getUserId(),
                    req.getName(),
                    req.getAddress(),
                    req.getPhone());

            OrderResponse orderResponse = OrderResponse.from(order);

            restResponse.setStatusCode(HttpStatus.CREATED.value());
            restResponse.setMessage("Đặt hàng thành công");
            restResponse.setData(orderResponse);
            restResponse.setError(null);

            return ResponseEntity.status(HttpStatus.CREATED).body(restResponse);

        } catch (RuntimeException ex) {
            // Trường hợp hết hàng hoặc lỗi liên quan đến sản phẩm
            restResponse.setStatusCode(HttpStatus.CONFLICT.value());
            restResponse.setMessage("Đặt hàng thất bại");
            restResponse.setData(null);
            restResponse.setError(ex.getMessage());

            return ResponseEntity.status(HttpStatus.CONFLICT).body(restResponse);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<RestResponse<List<OrderSummaryDTO>>> getAllOrders() {
        List<OrderSummaryDTO> orders = orderService.getAllOrderSummaries();

        RestResponse<List<OrderSummaryDTO>> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Lấy danh sách đơn hàng thành công");
        response.setData(orders);
        response.setError(null); // không có lỗi

        return ResponseEntity.ok(response);
    }

    // người dùng khi đặt hàng xong có thể hủy đơn hàng đó .
    @PostMapping("/{id}/cancel")
    public ResponseEntity<RestResponse<Void>> cancelOrder(@PathVariable Long id, Principal principal) {
        orderService.cancelOrder(id, principal.getName());

        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Đơn hàng đã được hủy");
        response.setData(null); // không trả về dữ liệu cụ thể
        response.setError(null);

        return ResponseEntity.ok(response);
    }

    // get order by user
    @GetMapping("/my-orders")
    public ResponseEntity<RestResponse<List<OrderSummaryDTO>>> getMyOrders(Principal principal) {
        String email = principal.getName();
        List<OrderSummaryDTO> orderSummaries = orderService.getOrderSummariesForUser(email);

        RestResponse<List<OrderSummaryDTO>> response = new RestResponse<>();
        response.setStatusCode(200); // Set status code
        response.setData(orderSummaries); // Set data
        response.setMessage("Danh sách đơn hàng"); // Có thể dùng thông điệp tùy ý

        return ResponseEntity.ok(response);
    }

    // người dùng khi click vào orderId có thể xem chi tiết sản phẩm
    @GetMapping("/{orderId}/details")
    public ResponseEntity<RestResponse<OrderHistoryDTO>> getOrderDetails(@PathVariable Long orderId,
            Principal principal) {
        OrderHistoryDTO dto = orderService.getOrderDetails(orderId, principal.getName());

        RestResponse<OrderHistoryDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Chi tiết đơn hàng");
        response.setData(dto);
        response.setError(null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<RestResponse<Void>> checkout(@RequestBody List<OrderItemDTO> orderItems) {
        try {
            orderService.processOrder(orderItems);
            RestResponse<Void> response = new RestResponse<>();
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Checkout successful");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            RestResponse<Void> errorResponse = new RestResponse<>();
            errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError("Checkout failed");
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

}
