package com.vn.capstone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    Optional<Order> findByPaymentRef(String paymentRef);

    @Query("SELECT SUM(o.totalPrice) FROM Order o")
    Double sumTotalPrice();

    @Query("SELECT SUM(od.quantity) FROM OrderDetail od JOIN od.order o WHERE o.status = 'CANCELED'")
    Long sumCanceledOrderQuantity();

    // void deleteByProductId(Long productId);
}