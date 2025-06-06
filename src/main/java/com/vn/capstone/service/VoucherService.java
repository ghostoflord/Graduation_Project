package com.vn.capstone.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vn.capstone.domain.User;
import com.vn.capstone.domain.Voucher;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.voucher.VoucherDTO;
import com.vn.capstone.domain.response.voucher.VoucherRequest;
import com.vn.capstone.repository.UserRepository;
import com.vn.capstone.repository.VoucherRepository;
import com.vn.capstone.util.error.VoucherException;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository; // dùng khi gán user

    public VoucherService(VoucherRepository voucherRepository, UserRepository userRepository) {
        this.voucherRepository = voucherRepository;
        this.userRepository = userRepository;
    }

    public Voucher createVoucher(VoucherRequest request) {
        if (request.getCode() == null || request.getCode().isEmpty()) {
            throw new VoucherException("Voucher code is required");
        }

        if (voucherRepository.existsByCode(request.getCode())) {
            throw new VoucherException("Voucher code already exists");
        }

        Voucher voucher = new Voucher();
        voucher.setCode(request.getCode());
        voucher.setDescription(request.getDescription());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setPercentage(request.isPercentage());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setSingleUse(request.isSingleUse());
        voucher.setCreatedAt(Instant.now());
        voucher.setUpdatedAt(Instant.now());

        return voucherRepository.save(voucher);
    }

    public void assignVoucherToUser(Long voucherId, Long userId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        voucher.setAssignedUser(user);
        voucherRepository.save(voucher);
    }

    public List<VoucherDTO> getAvailableVouchersForUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }

        LocalDateTime now = LocalDateTime.now();
        return voucherRepository.findAll().stream()
                .filter(v -> v.isActive()
                        && !v.isUsed()
                        && (v.getStartDate() == null || !v.getStartDate().isAfter(now))
                        && (v.getEndDate() == null || !v.getEndDate().isBefore(now))
                        && (v.getAssignedUser() == null || Long.valueOf(v.getAssignedUser().getId()).equals(userId)))
                .map(v -> {
                    VoucherDTO dto = new VoucherDTO();
                    dto.setId(v.getId());
                    dto.setCode(v.getCode());
                    dto.setDescription(v.getDescription());
                    dto.setDiscountValue(v.getDiscountValue());
                    dto.setPercentage(v.isPercentage());
                    dto.setStartDate(v.getStartDate());
                    dto.setEndDate(v.getEndDate());

                    if (v.getAssignedUser() != null) {
                        dto.setAssignedUser(
                                new VoucherDTO.AssignedUserDTO(
                                        v.getAssignedUser().getId(),
                                        v.getAssignedUser().getName(),
                                        v.getAssignedUser().getEmail()));
                    }

                    return dto;
                })
                .toList();
    }

    public int applyVoucher(String code, Long userId, int orderTotal) {
        Voucher voucher = voucherRepository.findByCode(code);

        if (!voucher.isActive())
            throw new RuntimeException("Voucher is not active");
        if (voucher.isSingleUse() && voucher.isUsed())
            throw new RuntimeException("Voucher already used");
        if (voucher.getStartDate().isAfter(LocalDateTime.now()) || voucher.getEndDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Voucher expired");
        if (voucher.getAssignedUser() != null && !Long.valueOf(voucher.getAssignedUser().getId()).equals(userId))
            throw new RuntimeException("Voucher not assigned to this user");

        int discount = voucher.isPercentage()
                ? orderTotal * voucher.getDiscountValue() / 100
                : voucher.getDiscountValue();

        // Đánh dấu đã dùng nếu là mã dùng 1 lần
        if (voucher.isSingleUse()) {
            voucher.setUsed(true);
            voucherRepository.save(voucher);
        }

        return discount;
    }

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public void deleteVoucher(Long id) {
        voucherRepository.deleteById(id);
    }
}
