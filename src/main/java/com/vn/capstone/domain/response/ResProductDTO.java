package com.vn.capstone.domain.response;

import java.time.Instant;

import com.vn.capstone.util.constant.GenderEnum;
import com.vn.capstone.util.constant.GuaranteeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResProductDTO {

    private long id;
    private String name;
    private String productCode;
    private String detailDescription;
    // in_stock,out_of_stock
    private GuaranteeEnum guarantee;
    private String image;

    private String factory;
    private String price;
    private String sold;
    private String quantity;
    private String shortDescription;

}
