package com.vn.capstone.domain.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.vn.capstone.domain.Product;

public class ProductSpecifications {

    public static Specification<Product> notInFlashSale(List<Long> flashSaleProductIds) {
        return (root, query, cb) -> {
            if (flashSaleProductIds == null || flashSaleProductIds.isEmpty()) {
                return cb.conjunction();
            }
            return cb.not(root.get("id").in(flashSaleProductIds));
        };
    }
}