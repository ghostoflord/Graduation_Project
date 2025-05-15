package com.vn.capstone.domain.response.product;

import com.vn.capstone.util.constant.BestsellEnum;
import com.vn.capstone.util.constant.GuaranteeEnum;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class ProductUpdateRequest {
    private long id;
    private String name;
    private String productCode;
    private String detailDescription;
    private GuaranteeEnum guarantee;
    private String factory;
    private String price;
    private String sold;
    private String quantity;
    private String shortDescription;
    private String image; // base64-encoded image (optional)
    @Enumerated(EnumType.STRING)
    private BestsellEnum bestsell;
    private String sell;
}
