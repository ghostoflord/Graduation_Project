package com.vn.capstone.domain.response.product;

import com.vn.capstone.util.constant.BestsellEnum;
import com.vn.capstone.util.constant.GuaranteeEnum;
import com.vn.capstone.util.constant.ProductCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private long id;
    private String name;
    private String productCode;
    private String detailDescription;
    private GuaranteeEnum guarantee;
    private String image;
    private String factory;
    private String price;
    private String sold;
    private String quantity;
    private String shortDescription;
    private String slug;
    private String sell;
    private BestsellEnum bestsell;
    private ProductCategoryEnum category;

    private ProductDetailDTO detail; // Thông tin chi tiết đi kèm
}
