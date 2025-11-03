package com.vn.capstone.domain.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import com.vn.capstone.domain.Product;

public class ProductSpecifications {

    // Lọc sản phẩm không nằm trong flash sale
    public static Specification<Product> notInFlashSale(List<Long> flashSaleProductIds) {
        return (root, query, cb) -> {
            if (flashSaleProductIds == null || flashSaleProductIds.isEmpty()) {
                return cb.conjunction();
            }
            return cb.not(root.get("id").in(flashSaleProductIds));
        };
    }

    // Lọc theo chi tiết sản phẩm (ProductDetail)
    public static Specification<Product> matchDetail(
            String cpu,
            String ram,
            String storage,
            String gpu) {
        return (root, query, cb) -> {
            Join<Object, Object> detailJoin = root.join("productDetail", JoinType.LEFT);
            List<Predicate> predicates = new ArrayList<>();

            if (cpu != null && !cpu.isEmpty()) {
                predicates.add(cb.like(cb.lower(detailJoin.get("cpu")), "%" + cpu.toLowerCase() + "%"));
            }
            if (ram != null && !ram.isEmpty()) {
                predicates.add(cb.like(cb.lower(detailJoin.get("ram")), "%" + ram.toLowerCase() + "%"));
            }
            if (storage != null && !storage.isEmpty()) {
                predicates.add(cb.like(cb.lower(detailJoin.get("storage")), "%" + storage.toLowerCase() + "%"));
            }
            if (gpu != null && !gpu.isEmpty()) {
                predicates.add(cb.like(cb.lower(detailJoin.get("gpu")), "%" + gpu.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}