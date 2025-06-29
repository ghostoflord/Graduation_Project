package com.vn.capstone.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vn.capstone.domain.User;
import com.vn.capstone.domain.UserVoucher;
import com.vn.capstone.domain.Voucher;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.domain.response.order.OrderDiscountResult;
import com.vn.capstone.domain.response.voucher.VoucherDTO;
import com.vn.capstone.domain.response.voucher.VoucherRequest;
import com.vn.capstone.domain.response.voucher.VoucherUpdateDTO;
import com.vn.capstone.repository.UserRepository;
import com.vn.capstone.repository.UserVoucherRepository;
import com.vn.capstone.repository.VoucherRepository;
import com.vn.capstone.util.error.VoucherException;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository; // dùng khi gán user
    private final UserVoucherRepository userVoucherRepository;

    public VoucherService(VoucherRepository voucherRepository, UserRepository userRepository,
            UserVoucherRepository userVoucherRepository) {
        this.voucherRepository = voucherRepository;
        this.userRepository = userRepository;
        this.userVoucherRepository = userVoucherRepository;
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

    public OrderDiscountResult applyVoucher(String code, Long userId, int orderTotal, boolean saveUsage) {
        Voucher voucher = voucherRepository.findByCode(code);

        if (voucher == null || !voucher.isActive()) {
            throw new RuntimeException("Voucher không tồn tại hoặc không còn hiệu lực");
        }

        // Check hạn sử dụng
        LocalDateTime now = LocalDateTime.now();
        if (voucher.getStartDate().isAfter(now) || voucher.getEndDate().isBefore(now)) {
            throw new RuntimeException("Voucher đã hết hạn hoặc chưa bắt đầu");
        }

        // Nếu voucher chỉ áp dụng cho 1 user cụ thể
        if (voucher.getAssignedUser() != null && !Long.valueOf(voucher.getAssignedUser().getId()).equals(userId)) {
            throw new RuntimeException("Voucher không áp dụng cho bạn");
        }

        // Xử lý SINGLE-USE
        if (voucher.isSingleUse()) {
            if (voucher.getAssignedUser() != null) {
                // Trường hợp dành riêng cho 1 user
                if (voucher.isUsed()) {
                    throw new RuntimeException("Bạn đã dùng voucher này rồi");
                }

                if (saveUsage) {
                    voucher.setUsed(true);
                    voucherRepository.save(voucher);
                }
            } else {
                // Dùng chung → check trong bảng user_voucher
                boolean alreadyUsed = userVoucherRepository.existsByUserIdAndVoucherId(userId, voucher.getId());
                if (alreadyUsed) {
                    throw new RuntimeException("Bạn đã dùng voucher này rồi");
                }

                if (saveUsage) {
                    UserVoucher uv = new UserVoucher();
                    uv.setUser(userRepository.getReferenceById(userId));
                    uv.setVoucher(voucher);
                    uv.setUsedAt(Instant.now());
                    userVoucherRepository.save(uv);
                }
            }
        }

        // Tính giảm giá
        long discount = voucher.isPercentage()
                ? orderTotal * voucher.getDiscountValue() / 100
                : voucher.getDiscountValue();

        if (discount > orderTotal) {
            discount = orderTotal;
        }

        long finalAmount = orderTotal - discount;

        return new OrderDiscountResult(discount, finalAmount);
    }

    public ResultPaginationDTO fetchAllVouchers(Specification<Voucher> spec, Pageable pageable) {
        Page<Voucher> page = voucherRepository.findAll(pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        List<VoucherDTO> result = page.getContent()
                .stream()
                .map(this::convertToResVoucherDTO)
                .collect(Collectors.toList());

        ResultPaginationDTO rs = new ResultPaginationDTO();
        rs.setMeta(meta);
        rs.setResult(result);
        return rs;
    }

    ///
    public VoucherDTO convertToResVoucherDTO(Voucher v) {
        VoucherDTO dto = new VoucherDTO();
        dto.setId(v.getId());
        dto.setCode(v.getCode());
        dto.setDescription(v.getDescription());
        dto.setDiscountValue(v.getDiscountValue());
        dto.setPercentage(v.isPercentage());
        dto.setStartDate(v.getStartDate());
        dto.setEndDate(v.getEndDate());
        // Không set assignedUser
        return dto;
    }

    public void deleteVoucher(Long id) {
        voucherRepository.deleteById(id);
    }

    //
    public Voucher updateVoucherFromDTO(Long id, VoucherUpdateDTO dto) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        voucher.setCode(dto.getCode());
        voucher.setDescription(dto.getDescription());
        voucher.setDiscountValue(dto.getDiscountValue());
        voucher.setPercentage(dto.isPercentage());
        voucher.setStartDate(dto.getStartDate());
        voucher.setEndDate(dto.getEndDate());
        voucher.setSingleUse(dto.isSingleUse());
        voucher.setActive(dto.isActive());
        voucher.setUsed(dto.isUsed());

        if (dto.getAssignedUserId() != null) {
            User user = userRepository.findById(dto.getAssignedUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            voucher.setAssignedUser(user);
        } else {
            voucher.setAssignedUser(null);
        }

        voucher.setUpdatedAt(Instant.now());

        return voucherRepository.save(voucher);
    }

}
