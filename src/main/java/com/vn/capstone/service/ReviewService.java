package com.vn.capstone.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.Review;
import com.vn.capstone.domain.User;
import com.vn.capstone.repository.ProductRepository;
import com.vn.capstone.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public Review addOrUpdateReview(Long productId, Long userId, float rating) {
        Optional<Review> existing = reviewRepository.findByProductIdAndUserId(productId, userId);
        Review review = existing.orElse(new Review());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        review.setProduct(product);
        review.setUser(new User(userId));
        review.setRating(rating);

        if (existing.isPresent()) {
            review.setUpdatedAt(Instant.now());
        } else {
            review.setCreatedAt(Instant.now());
        }

        return reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
