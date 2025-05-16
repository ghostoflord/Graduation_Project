package com.vn.capstone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.CartDetail;

import jakarta.transaction.Transactional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    List<CartDetail> findByCartId(Long cartId);

    @Query("select coalesce(sum(cd.quantity),0) from CartDetail cd where cd.cart.id = :cartId")
    Long sumQuantityByCart(@Param("cartId") long cartId);

    void deleteByCartUserIdAndProductId(Long userId, Long productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartDetail cd WHERE cd.cart.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
