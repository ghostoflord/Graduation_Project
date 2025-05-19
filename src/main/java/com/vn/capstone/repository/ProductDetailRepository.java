package com.vn.capstone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.ProductDetail;

@Repository
public interface ProductDetailRepository
        extends JpaRepository<ProductDetail, Long>, JpaSpecificationExecutor<ProductDetail> {
    ProductDetail findByProductId(Long productId);
    
}
