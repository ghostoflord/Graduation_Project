package com.vn.capstone.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.Review;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.review.ReviewDTO;
import com.vn.capstone.service.ReviewService;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/product/{productId}")
    public RestResponse<List<Review>> getReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProduct(productId);
        RestResponse<List<Review>> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Lấy danh sách đánh giá thành công");
        response.setData(reviews);
        return response;
    }

    @PostMapping
    public RestResponse<Review> addReview(@RequestBody ReviewDTO dto) {
        Review review = reviewService.addOrUpdateReview(
                dto.getProductId(),
                dto.getUserId(),
                dto.getRating());
        RestResponse<Review> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Thêm hoặc cập nhật đánh giá thành công");
        response.setData(review);
        return response;
    }

    @DeleteMapping("/{id}")
    public RestResponse<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Xóa đánh giá thành công");
        response.setData(null);
        return response;
    }
}
