package com.vn.capstone.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vn.capstone.domain.Notification;
import com.vn.capstone.domain.User;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.domain.response.notification.NotificationCreateDTO;
import com.vn.capstone.domain.response.notification.NotificationDTO;
import com.vn.capstone.repository.NotificationRepository;
import com.vn.capstone.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void createNotification(Long userId, String title, String content) {
        if (userId == null) {
            // Gửi cho tất cả user
            List<User> users = userRepository.findAll();
            List<Notification> notifications = new ArrayList<>();

            for (User user : users) {
                Notification noti = new Notification();
                noti.setUser(user);
                noti.setTitle(title);
                noti.setContent(content);
                notifications.add(noti);
            }

            notificationRepository.saveAll(notifications);
        } else {
            // Gửi cho 1 user cụ thể
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Notification noti = new Notification();
            noti.setUser(user);
            noti.setTitle(title);
            noti.setContent(content);
            notificationRepository.save(noti);
        }
    }

    public List<NotificationDTO> getNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(noti -> {
            noti.setIsRead(true);
            notificationRepository.save(noti);
        });
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setIsRead(notification.getIsRead());
        return dto;
    }

    public ResultPaginationDTO getAllNotifications(Specification<Notification> spec, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(notifications.getTotalPages());
        meta.setTotal(notifications.getTotalElements());

        result.setMeta(meta);

        List<NotificationCreateDTO> list = notifications.getContent()
                .stream()
                .map(notification -> {
                    NotificationCreateDTO dto = new NotificationCreateDTO();
                    dto.setId(notification.getId());
                    dto.setTitle(notification.getTitle());
                    dto.setContent(notification.getContent());
                    dto.setRead(Boolean.TRUE.equals(notification.getIsRead()));
                    dto.setForAll(notification.isForAll());
                    dto.setCreatedAt(notification.getCreatedAt());
                    if (notification.getUser() != null) {
                        dto.setUserId(notification.getUser().getId());
                        dto.setUserName(notification.getUser().getName());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        result.setResult(list);
        return result;
    }

}