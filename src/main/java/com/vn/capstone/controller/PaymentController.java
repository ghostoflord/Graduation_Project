package com.vn.capstone.controller;

import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.service.OrderService;
import com.vn.capstone.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final VNPayService vnPayService;
    private final OrderService orderService;

    public PaymentController(VNPayService vnPayService, OrderService orderService) {
        this.vnPayService = vnPayService;
        this.orderService = orderService;
    }

    // Lưu tạm thông tin người nhận theo paymentRef
    private static final Map<String, ReceiverInfo> receiverInfoMap = new ConcurrentHashMap<>();

    // Class lưu thông tin người nhận
    private static class ReceiverInfo {
        String name;
        String address;
        String phone;

        ReceiverInfo(String name, String address, String phone) {
            this.name = name;
            this.address = address;
            this.phone = phone;
        }
    }

    @PostMapping("/payment/vnpay")
    public ResponseEntity<RestResponse<String>> createVNPayUrl(@RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        RestResponse<String> restResponse = new RestResponse<>();

        try {
            // Lấy và kiểm tra tham số
            Double amount = parseDouble(request.get("amount"));
            String paymentRef = parseString(request.get("paymentRef"));
            Long userId = parseLong(request.get("userId"));
            String receiverName = parseString(request.get("name"));
            String receiverAddress = parseString(request.get("address"));
            String receiverPhone = parseString(request.get("phone"));

            if (amount == null || paymentRef == null || userId == null || receiverName == null
                    || receiverAddress == null || receiverPhone == null) {
                restResponse.setStatusCode(400);
                restResponse.setError("Thiếu dữ liệu trong request");
                restResponse.setMessage("Yêu cầu phải có đầy đủ thông tin thanh toán và người nhận");
                return ResponseEntity.badRequest().body(restResponse);
            }

            // Lưu tạm thông tin người nhận
            receiverInfoMap.put(paymentRef, new ReceiverInfo(receiverName, receiverAddress, receiverPhone));

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

    @PostMapping("/payment/vnpay/response")
    public ResponseEntity<RestResponse<String>> handleVNPAYResponse(@RequestBody Map<String, Object> request) {
        RestResponse<String> restResponse = new RestResponse<>();

        try {
            Double amount = parseDouble(request.get("amount"));
            String paymentRef = parseString(request.get("paymentRef"));
            Long userId = parseLong(request.get("userId"));
            String paymentStatus = parseString(request.get("paymentStatus"));

            if (amount == null || paymentRef == null || userId == null || paymentStatus == null) {
                restResponse.setStatusCode(400);
                restResponse.setError("Thiếu dữ liệu trong phản hồi");
                restResponse.setMessage("Phản hồi phải bao gồm userId, amount, paymentRef và paymentStatus");
                return ResponseEntity.badRequest().body(restResponse);
            }

            // Lấy thông tin người nhận từ map tạm
            ReceiverInfo receiverInfo = receiverInfoMap.get(paymentRef);
            if (receiverInfo == null) {
                restResponse.setStatusCode(404);
                restResponse.setError("Không tìm thấy thông tin người nhận");
                restResponse.setMessage("Không thể xử lý phản hồi vì thiếu thông tin người nhận");
                return ResponseEntity.status(404).body(restResponse);
            }

            amount = amount / 100.0;

            if ("success".equalsIgnoreCase(paymentStatus)) {
                try {
                    orderService.handleVNPAYSuccess(
                            userId,
                            paymentRef,
                            amount,
                            receiverInfo.name,
                            receiverInfo.address,
                            receiverInfo.phone);

                    // Xoá thông tin sau khi đã xử lý xong
                    receiverInfoMap.remove(paymentRef);

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
