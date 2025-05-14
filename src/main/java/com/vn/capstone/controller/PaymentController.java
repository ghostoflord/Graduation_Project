package com.vn.capstone.controller;

import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.service.OrderService;
import com.vn.capstone.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final VNPayService vnPayService;
    private final OrderService orderService;

    public PaymentController(VNPayService vnPayService, OrderService orderService) {
        this.vnPayService = vnPayService;
        this.orderService = orderService;
    }

    @PostMapping("/vnpay")
    public ResponseEntity<RestResponse<String>> createVNPayUrl(@RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        RestResponse<String> restResponse = new RestResponse<>();

        try {
            // Lấy và kiểm tra tham số
            Double amount = parseDouble(request.get("amount"));
            String paymentRef = parseString(request.get("paymentRef"));
            Long userId = parseLong(request.get("userId"));

            if (amount == null || paymentRef == null || userId == null) {
                restResponse.setStatusCode(400);
                restResponse.setError("Thiếu dữ liệu trong request");
                restResponse.setMessage("Yêu cầu phải có 'amount', 'paymentRef' và 'userId'");
                return ResponseEntity.badRequest().body(restResponse);
            }

            // Chia amount cho 100 để đưa về đúng giá trị
            // amount = amount / 100.0;

            String ip = vnPayService.getIpAddress(httpRequest);
            String paymentUrl = vnPayService.generateVNPayURL(amount, paymentRef, ip, userId);

            restResponse.setStatusCode(200);
            restResponse.setMessage("Tạo link thanh toán thành công");
            restResponse.setData(paymentUrl);
            return ResponseEntity.ok(restResponse);

        } catch (Exception e) {
            e.printStackTrace();
            restResponse.setStatusCode(500);
            restResponse.setError("Lỗi tạo link thanh toán: " + e.getMessage());
            restResponse.setMessage("Không thể tạo liên kết thanh toán");
            return ResponseEntity.status(500).body(restResponse);
        }
    }

    @PostMapping("/vnpay/response")
    public ResponseEntity<RestResponse<String>> handleVNPAYResponse(@RequestBody Map<String, Object> request) {
        RestResponse<String> restResponse = new RestResponse<>();

        try {
            // Lấy và kiểm tra tham số
            Double amount = parseDouble(request.get("amount"));
            String paymentRef = parseString(request.get("paymentRef"));
            Long userId = parseLong(request.get("userId"));
            String paymentStatus = parseString(request.get("paymentStatus"));

            if (amount == null || paymentRef == null || userId == null || paymentStatus == null) {
                restResponse.setStatusCode(400);
                restResponse.setError("Thiếu dữ liệu trong phản hồi");
                restResponse.setMessage("Phản hồi phải bao gồm 'amount', 'paymentRef', 'userId', 'paymentStatus'");
                return ResponseEntity.badRequest().body(restResponse);
            }

            // Chia amount cho 100 để đưa về đúng giá trị
            amount = amount / 100.0;

            System.out.println("===> VNPAY callback: amount=" + amount + ", ref=" + paymentRef
                    + ", userId=" + userId + ", status=" + paymentStatus);

            if ("success".equalsIgnoreCase(paymentStatus)) {
                try {
                    orderService.handleVNPAYSuccess(userId, paymentRef, amount);
                    restResponse.setStatusCode(200);
                    restResponse.setMessage("Thanh toán thành công và đơn hàng đã được lưu");
                    return ResponseEntity.ok(restResponse);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    restResponse.setStatusCode(500);
                    restResponse.setError("Lỗi khi lưu đơn hàng: " + ex.getMessage());
                    restResponse.setMessage("Có lỗi xảy ra khi lưu đơn hàng.");
                    return ResponseEntity.status(500).body(restResponse);
                }
            } else {
                restResponse.setStatusCode(400);
                restResponse.setError("Thanh toán thất bại");
                restResponse.setMessage("Thanh toán không thành công, vui lòng thử lại");
                return ResponseEntity.badRequest().body(restResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
            restResponse.setStatusCode(500);
            restResponse.setError("Lỗi xử lý callback VNPAY: " + e.getMessage());
            restResponse.setMessage("Không thể xử lý phản hồi thanh toán.");
            return ResponseEntity.status(500).body(restResponse);
        }
    }

    ///
    private Double parseDouble(Object value) {
        if (value == null)
            return null;
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String parseString(Object value) {
        return value == null ? null : value.toString();
    }

    private Long parseLong(Object value) {
        if (value == null)
            return null;
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
