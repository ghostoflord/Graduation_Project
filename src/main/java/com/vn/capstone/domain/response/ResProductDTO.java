package com.vn.capstone.domain.response;

import java.util.List;

import com.vn.capstone.util.constant.BestsellEnum;
import com.vn.capstone.util.constant.GuaranteeEnum;
import com.vn.capstone.util.constant.ProductCategoryEnum;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private String slide;
    private String factory;
    private String price;
    private String sold;
    private String quantity;
    private String shortDescription;
    @Enumerated(EnumType.STRING)
    private BestsellEnum bestsell;
    @Enumerated(EnumType.STRING)
    private ProductCategoryEnum category;
    private String sell;
    private String discountPrice;
    //
    private Double averageRating;
    private Long totalReviews;

    private List<String> images;
}
