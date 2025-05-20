package com.vn.capstone.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vn.capstone.repository.LikeRepository;
import com.vn.capstone.repository.ProductRepository;
import com.vn.capstone.repository.UserRepository;
import com.vn.capstone.domain.Like;
import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.User;

@Service
public class LikeService {

    private final LikeRepository likeRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    public LikeService(LikeRepository likeRepository, ProductRepository productRepository,
            UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // Thêm like hoặc bỏ like nếu đã like rồi
    public boolean toggleLike(Long productId, Long userId) {
        Optional<Like> existingLike = likeRepository.findByProductIdAndUserId(productId, userId);

        if (existingLike.isPresent()) {
            // Nếu đã like, thì bỏ like (unlike)
            likeRepository.delete(existingLike.get());
            return false; // báo bỏ like
        } else {
            // Thêm like mới
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Like like = new Like();
            like.setProduct(product);
            like.setUser(user);
            like.setCreatedAt(Instant.now());

            likeRepository.save(like);
            return true; // báo đã like
        }
    }

    // Lấy số lượng like của sản phẩm
    public int countLikes(Long productId) {
        return likeRepository.countByProductId(productId);
    }
}
