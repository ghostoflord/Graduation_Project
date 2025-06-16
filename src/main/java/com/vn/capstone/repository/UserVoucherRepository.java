package com.vn.capstone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.UserVoucher;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, Long> {
    boolean existsByUserIdAndVoucherId(Long userId, Long voucherId);
}
