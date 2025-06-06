package com.vn.capstone.domain.response.voucher;

import lombok.Data;

@Data
public class VoucherApplyRequest {
    private String code;
    private Long userId;
    private int orderTotal;
}
