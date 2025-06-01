package com.vn.capstone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vn.capstone.domain.Notification;
import com.vn.capstone.domain.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);
}