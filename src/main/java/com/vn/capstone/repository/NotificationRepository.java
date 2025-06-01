package com.vn.capstone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.vn.capstone.domain.Notification;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);
}