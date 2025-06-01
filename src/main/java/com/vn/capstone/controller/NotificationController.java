package com.vn.capstone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.service.NotificationService;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<RestResponse<?>> getUserNotifications(@RequestParam Long userId) {
        RestResponse<Object> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Lấy danh sách thông báo thành công");
        response.setError(null);
        response.setData(notificationService.getNotificationsForUser(userId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/notifications/{id}/read")
    public ResponseEntity<RestResponse<?>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        RestResponse<Object> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Đánh dấu thông báo đã đọc thành công");
        response.setError(null);
        response.setData(null); // Không trả dữ liệu cụ thể
        return ResponseEntity.ok(response);
    }

    @PostMapping("/notifications/create")
    // @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<RestResponse<?>> createNotification(
            @RequestParam(required = false) Long userId,
            @RequestParam String title,
            @RequestParam String content) {

        notificationService.createNotification(userId, title, content);

        RestResponse<Object> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage(
                userId == null ? "Đã gửi thông báo đến tất cả người dùng" : "Đã gửi thông báo đến người dùng");
        response.setError(null);
        response.setData(null);
        return ResponseEntity.ok(response);
    }

}
