package com.vn.capstone.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.domain.response.order.OrderHistoryDTO;
import com.vn.capstone.domain.response.order.OrderItemDTO;
import com.vn.capstone.domain.response.order.OrderResponse;
import com.vn.capstone.domain.response.order.OrderShipperDTO;
import com.vn.capstone.domain.response.order.OrderSummaryDTO;
import com.vn.capstone.domain.response.order.PlaceOrderRequest;
import com.vn.capstone.domain.response.order.UpdateOrderRequest;
import com.vn.capstone.service.OrderService;
import com.vn.capstone.service.ProductService;
import com.vn.capstone.util.annotation.ApiMessage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;

    public OrderController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<OrderSummaryDTO>> getOrderSummaryById(@PathVariable Long id) {
        var restResponse = new RestResponse<OrderSummaryDTO>();
        return orderService.getOrderSummaryById(id)
                .map(dto -> {
                    restResponse.setStatusCode(200);
                    restResponse.setError(null);
                    restResponse.setMessage("Success");
                    restResponse.setData(dto);
                    return ResponseEntity.ok(restResponse);
                })
                .orElseGet(() -> {
                    restResponse.setStatusCode(404);
                    restResponse.setError("Not Found");
                    restResponse.setMessage("Order with id " + id + " not found");
                    restResponse.setData(null);
                    return ResponseEntity.status(404).body(restResponse);
                });
    }

    @PostMapping("/place")
    public ResponseEntity<RestResponse<OrderResponse>> placeOrder(@Valid @RequestBody PlaceOrderRequest req) {
        RestResponse<OrderResponse> restResponse = new RestResponse<>();

        try {
            Order order = orderService.placeOrder(
                    req.getUserId(),
                    req.getName(),
                    req.getAddress(),
                    req.getPhone(),
                    req.getVoucherCode());

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

    // cập nhập order bởi admin
    @PostMapping("/{id}/update")
    public ResponseEntity<RestResponse<Void>> updateOrder(@PathVariable Long id,
            @RequestBody UpdateOrderRequest request,
            Principal principal) {
        orderService.updateOrder(id, request, principal.getName());

        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Cập nhật đơn hàng thành công");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse<Void>> deleteOrder(@PathVariable("id") Long orderId) {
        RestResponse<Void> response = new RestResponse<>();
        orderService.deleteOrderById(orderId);
        response.setStatusCode(HttpStatus.NO_CONTENT.value());
        response.setMessage("Order deleted successfully");
        response.setData(null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shipper")
    @ApiMessage("fetch orders for shipper")
    public ResponseEntity<RestResponse<ResultPaginationDTO>> getOrdersForShipper(
            @Filter Specification<Order> spec,
            Pageable pageable,
            Authentication authentication) {

        String username = authentication.getName();

        ResultPaginationDTO result = orderService.fetchOrdersForShipper(username, spec, pageable);

        RestResponse<ResultPaginationDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("fetch orders for shipper");
        response.setData(result);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{orderId}/accept")
    public ResponseEntity<RestResponse<Void>> acceptOrder(@PathVariable Long orderId, Authentication auth) {
        orderService.acceptOrder(orderId, auth.getName());
        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Đã nhận đơn hàng thành công");
        response.setData(null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<RestResponse<Void>> completeOrder(@PathVariable Long orderId, Authentication auth) {
        orderService.completeOrder(orderId, auth.getName());
        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Đã hoàn tất đơn hàng thành công");
        response.setData(null);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/delivered")
    public ResponseEntity<RestResponse<Void>> markAsDelivered(@PathVariable Long orderId) {
        orderService.markAsDelivered(orderId);

        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Cập nhật trạng thái thành công");
        response.setData(null); // Không có data trả về cụ thể

        return ResponseEntity.ok(response);
    }

    @GetMapping("/shipper/delivered")
    public ResponseEntity<RestResponse<List<OrderShipperDTO>>> getDeliveredOrdersForShipper(
            Authentication authentication) {
        String username = authentication.getName();
        List<OrderShipperDTO> orders = orderService.getDeliveredOrdersForShipper(username);

        RestResponse<List<OrderShipperDTO>> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Lấy danh sách đơn hàng đã giao thành công");
        response.setData(orders);

        return ResponseEntity.ok(response);
    }

}
