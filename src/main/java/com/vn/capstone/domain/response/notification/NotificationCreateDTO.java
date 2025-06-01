package com.vn.capstone.domain.response.notification;

import java.time.Instant;

import lombok.Data;

@Data
public class NotificationCreateDTO {
    private Long id;
    private String title;
    private String content;
    private boolean isRead;
    private boolean forAll;
    private Instant createdAt;
    private Long userId;
    private String userName;

}
