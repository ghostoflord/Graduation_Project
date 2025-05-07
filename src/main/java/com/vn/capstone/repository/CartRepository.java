package com.vn.capstone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.Cart;
import com.vn.capstone.domain.CartDetail;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserId(Long userId);

    Optional<Cart> findByUser_Id(Long userId);

    // List<CartDetail> findByCartId(Long cartId);
}