package com.vn.capstone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.Product;

import jakarta.persistence.LockModeType;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findBySlug(String slug);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Product findByIdForUpdate(@Param("productId") Long productId);

    List<Product> findAllByIdIn(List<Long> ids);

    @Query("SELECT p FROM Product p WHERE CAST(p.quantity AS int) - CAST(p.sold AS int) <= :threshold")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);

    @Query(value = "SELECT * FROM products p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY LOCATE(LOWER(:keyword), LOWER(p.name)) ASC " +
            "LIMIT 10", nativeQuery = true)
    List<Product> searchProducts(@Param("keyword") String keyword);
}
