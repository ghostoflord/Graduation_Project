package com.vn.capstone.domain.response.notification;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
