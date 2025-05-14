package com.vn.capstone.controller;

import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.service.OrderService;
import com.vn.capstone.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
// @RequiredArgsConstructor
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
            double amount = Double.parseDouble(request.get("amount").toString());
            String paymentRef = request.get("paymentRef").toString();
            Long userId = Long.parseLong(request.get("userId").toString());
            String ip = vnPayService.getIpAddress(httpRequest);

            // Truyền userId vào để VNPayService nhúng vào returnUrl
            String paymentUrl = vnPayService.generateVNPayURL(amount, paymentRef, ip, userId);

            restResponse.setStatusCode(200);
            restResponse.setMessage("Tạo link thanh toán thành công");
            restResponse.setData(paymentUrl);
            return ResponseEntity.ok(restResponse);

        } catch (Exception e) {
            restResponse.setStatusCode(400);
            restResponse.setError("Lỗi tạo link thanh toán");
            restResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(restResponse);
        }
    }

    @PostMapping("/vnpay/response")
    public ResponseEntity<RestResponse<String>> handleVNPAYResponse(@RequestBody Map<String, Object> request) {
        RestResponse<String> restResponse = new RestResponse<>();

        try {
            // Log toàn bộ request để debug
            System.out.println("===> Dữ liệu VNPAY gửi về: " + request);

            // Lấy thông tin từ request
            double amount = Double.parseDouble(request.get("amount").toString());
            String paymentRef = request.get("paymentRef").toString();
            Long userId = Long.parseLong(request.get("userId").toString());
            String paymentStatus = request.get("paymentStatus").toString();

            System.out.println("===> Đã parse: amount=" + amount + ", paymentRef=" + paymentRef + ", userId=" + userId
                    + ", paymentStatus=" + paymentStatus);

            if ("success".equals(paymentStatus)) {
                try {
                    // Gọi xử lý lưu đơn hàng
                    orderService.handleVNPAYSuccess(userId, paymentRef, amount);

                    restResponse.setStatusCode(200);
                    restResponse.setMessage("Thanh toán thành công và đơn hàng đã được lưu");
                    return ResponseEntity.ok(restResponse);
                } catch (Exception orderEx) {
                    // Ghi log lỗi xử lý đơn hàng
                    orderEx.printStackTrace();
                    restResponse.setStatusCode(500);
                    restResponse.setError("Lỗi khi lưu đơn hàng: " + orderEx.getMessage());
                    restResponse.setMessage("Có lỗi xảy ra khi lưu đơn hàng. Vui lòng liên hệ hỗ trợ.");
                    return ResponseEntity.status(500).body(restResponse);
                }
            } else {
                // Thanh toán thất bại
                restResponse.setStatusCode(400);
                restResponse.setError("Thanh toán thất bại");
                restResponse.setMessage("Thanh toán không thành công, vui lòng thử lại");
                return ResponseEntity.badRequest().body(restResponse);
            }

        } catch (Exception e) {
            // Ghi log lỗi parse hoặc lỗi khác
            e.printStackTrace();
            restResponse.setStatusCode(500);
            restResponse.setError("Lỗi xử lý dữ liệu từ VNPAY: " + e.getMessage());
            restResponse.setMessage("Không thể xử lý kết quả thanh toán. Dữ liệu không hợp lệ hoặc thiếu.");
            return ResponseEntity.status(500).body(restResponse);
        }
    }

}
