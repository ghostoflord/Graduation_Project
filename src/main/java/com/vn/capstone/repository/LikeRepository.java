package com.vn.capstone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByProductIdAndUserId(Long productId, Long userId);

    Long countByProductId(Long productId);

    void deleteByProductIdAndUserId(Long productId, Long userId);

    List<Like> findByUserId(Long userId);

    List<Like> findAllByUserId(Long userId);    
}
