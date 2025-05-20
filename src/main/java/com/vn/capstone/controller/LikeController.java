package com.vn.capstone.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.service.LikeService;

@RestController
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/toggle")
    public RestResponse<Map<String, Object>> toggleLike(
            @RequestParam Long productId,
            @RequestParam Long userId) {
        boolean liked = likeService.toggleLike(productId, userId);
        int totalLikes = likeService.countLikes(productId);

        Map<String, Object> data = new HashMap<>();
        data.put("liked", liked);
        data.put("totalLikes", totalLikes);

        RestResponse<Map<String, Object>> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Đã cập nhật trạng thái yêu thích sản phẩm");
        response.setData(data);
        return response;
    }

    // API lấy tổng số like của sản phẩm
    @GetMapping("/count/{productId}")
    public RestResponse<Integer> getLikeCount(@PathVariable Long productId) {
        int totalLikes = likeService.countLikes(productId);

        RestResponse<Integer> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Lấy số lượt thích thành công");
        response.setData(totalLikes);
        return response;
    }
}
