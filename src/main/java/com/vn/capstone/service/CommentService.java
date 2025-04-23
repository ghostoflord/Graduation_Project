package com.vn.capstone.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vn.capstone.domain.Comment;
import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.User;
import com.vn.capstone.repository.CommentRepository;
import com.vn.capstone.repository.ProductRepository;
import com.vn.capstone.repository.UserRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository,
            ProductRepository productRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Comment saveComment(Long userId, Long productId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setProduct(product);

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByProduct(Long productId) {
        return commentRepository.findByProductId(productId);
    }
}
