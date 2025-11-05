package com.vn.capstone.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.FlashSaleItem;

@Repository
public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Long> {
    List<FlashSaleItem> findByFlashSaleId(Long flashSaleId);

    @Query("""
            SELECT fsi FROM FlashSaleItem fsi
            JOIN fsi.flashSale fs
            WHERE fsi.product.id = :productId
            AND fs.status = 'ACTIVE'
            AND fs.startTime <= :now
            AND fs.endTime >= :now
            """)
    Optional<FlashSaleItem> findActiveFlashSaleItemForProduct(Long productId, LocalDateTime now);
}
