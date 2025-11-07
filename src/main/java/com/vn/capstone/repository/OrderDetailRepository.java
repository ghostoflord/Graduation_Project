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
        // Thống kê trong khoảng thời gian bất kỳ
        @Query("SELECT SUM(od.quantity) FROM OrderDetail od " +
                        "WHERE od.order.updatedAt >= :start AND od.order.updatedAt < :end " +
                        "AND od.order.status = :status")
        Long getTotalProductsByTimeRange(@Param("start") Instant start,
                        @Param("end") Instant end,
                        @Param("status") OrderStatus status);

        // Thống kê theo tháng trong năm
        @Query("SELECT SUM(od.quantity) FROM OrderDetail od " +
                        "WHERE FUNCTION('YEAR', od.order.createdAt) = :year " +
                        "AND FUNCTION('MONTH', od.order.createdAt) = :month " +
                        "AND od.order.status = :status")
        Long getTotalProductsByMonth(@Param("year") int year,
                        @Param("month") int month,
                        @Param("status") OrderStatus status);

        @Query("SELECT od.product.name, SUM(od.quantity), SUM(od.quantity * od.price) " +
                        "FROM OrderDetail od " +
                        "WHERE od.order.status = 'CONFIRMED' " +
                        "GROUP BY od.product.name " +
                        "ORDER BY SUM(od.quantity) DESC")
        List<Object[]> getTopSellingProducts();

}