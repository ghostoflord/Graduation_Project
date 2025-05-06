package com.vn.capstone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductId(Long productId);

    void deleteByProductId(Long productId);
}
