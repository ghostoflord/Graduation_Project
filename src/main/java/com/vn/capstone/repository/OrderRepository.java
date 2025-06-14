package com.vn.capstone.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.User;
import com.vn.capstone.util.constant.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
        List<Order> findByUser(User user);

        Optional<Order> findById(long id);
        // @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderDetails WHERE o.id =
        // :id")
        // Optional<Order> findWithDetailsById(@Param("id") Long id);

        Optional<Order> findByPaymentRef(String paymentRef);

        @Query("SELECT SUM(o.totalPrice) FROM Order o")
        Double sumTotalPrice();

        @Query("SELECT SUM(od.quantity) FROM OrderDetail od JOIN od.order o WHERE o.status = 'CANCELED'")
        Long sumCanceledOrderQuantity();

        List<Order> findByShipperAndStatus(User shipper, OrderStatus status);

        // void deleteByProductId(Long productId);

        @Query("SELECT COUNT(o) FROM Order o WHERE o.shipper.id = :shipperId AND o.status = :status")
        long countByStatusAndShipper(@Param("shipperId") Long shipperId,
                        @Param("status") OrderStatus status);

        @Query("SELECT COUNT(o) FROM Order o WHERE o.shipper.id = :shipperId AND o.status IN :statuses")
        long countByStatusesAndShipper(@Param("shipperId") Long shipperId,
                        @Param("statuses") List<OrderStatus> statuses);

        @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.shipper.id = :shipperId AND o.status = 'DELIVERED'")
        long sumCODByShipper(@Param("shipperId") Long shipperId);

        List<Order> findAllByUserId(Long userId);

}