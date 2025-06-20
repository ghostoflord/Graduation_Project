package com.vn.capstone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.FlashSale;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long>, JpaSpecificationExecutor<FlashSale> {
    List<FlashSale> findByStatus(String status);
}
