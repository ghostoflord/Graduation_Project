package com.vn.capstone.domain.response.voucher;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherApplyRequest {
    private String code;
    private Long userId;
    private int orderTotal;
}
