package com.vn.capstone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.FaqRule;

@Repository
public interface FaqRuleRepository extends JpaRepository<FaqRule, Long> {
    // Tạo method tìm theo keyword (ignore case và chứa chuỗi)
    Optional<FaqRule> findFirstByKeywordContainingIgnoreCase(String keyword);
}