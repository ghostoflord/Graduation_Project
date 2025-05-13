package com.vn.capstone.controller;

import com.vn.capstone.domain.response.RestResponse;
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

    public PaymentController(VNPayService vnPayService) {
        this.vnPayService = vnPayService;
    }

    @PostMapping("/vnpay")
    public ResponseEntity<RestResponse<String>> createVNPayUrl(@RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        RestResponse<String> restResponse = new RestResponse<>();

        try {
            // Lấy amount
            double amount = Double.parseDouble(request.get("amount").toString());

            // Lấy paymentRef dưới dạng String
            String paymentRef = request.get("paymentRef").toString();

            // Lấy IP người dùng
            String ip = vnPayService.getIpAddress(httpRequest);

            // Tạo link thanh toán
            String paymentUrl = vnPayService.generateVNPayURL(amount, paymentRef, ip);

            // Set thông tin trả về trong RestResponse
            restResponse.setStatusCode(200);
            restResponse.setMessage("Tạo link thanh toán thành công");
            restResponse.setData(paymentUrl);

            return ResponseEntity.ok(restResponse);
        } catch (Exception e) {
            // Set thông tin lỗi trong RestResponse
            restResponse.setStatusCode(400);
            restResponse.setError("Lỗi tạo link thanh toán");
            restResponse.setMessage(e.getMessage());

            return ResponseEntity.badRequest().body(restResponse);
        }
    }
}
