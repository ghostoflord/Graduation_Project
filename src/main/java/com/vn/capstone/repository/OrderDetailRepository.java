package com.vn.capstone.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.OrderDetail;
import com.vn.capstone.domain.response.product.ProductStatisticDTO;
import com.vn.capstone.util.constant.OrderStatus;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    void deleteByProductId(Long productId);

    List<OrderDetail> findByOrder(Order order);

    // Thống kê trong khoảng thời gian bất kỳ
    @Query("SELECT new com.vn.capstone.domain.response.product.ProductStatisticDTO(" +
            "od.product.id, od.product.name, SUM(od.quantity)) " +
            "FROM OrderDetail od " +
            "JOIN od.order o " +
            "WHERE o.createdAt >= :start AND o.createdAt < :end " +
            "AND o.status = :status " +
            "GROUP BY od.product.id, od.product.name " +
            "ORDER BY SUM(od.quantity) DESC")
    List<ProductStatisticDTO> getTopProductsByTimeRange(@Param("start") Instant start,
            @Param("end") Instant end,
            @Param("status") OrderStatus status);
}