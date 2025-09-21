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
        @Query("SELECT SUM(od.quantity) FROM OrderDetail od " +
                        "WHERE od.order.createdAt >= :start AND od.order.createdAt < :end " +
                        "AND od.order.status = :status")
        Long getTotalProductsByTimeRange(@Param("start") Instant start,
                        @Param("end") Instant end,
                        @Param("status") OrderStatus status);

}