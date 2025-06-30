package com.vn.capstone.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.Comment;
import com.vn.capstone.domain.response.CommentRequest;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.comment.CommentResponse;
import com.vn.capstone.service.CommentService;

@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/comments")
    public ResponseEntity<RestResponse<Comment>> createComment(@RequestBody CommentRequest request) {
        Comment comment = commentService.saveComment(
                request.getUserId(),
                request.getProductId(),
                request.getContent());

        RestResponse<Comment> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Tạo bình luận thành công");
        response.setData(comment);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/comments/product/{productId}")
    public ResponseEntity<RestResponse<List<CommentResponse>>> getCommentsByProduct(@PathVariable Long productId) {
        List<CommentResponse> commentResponses = commentService.getCommentsByProduct(productId);
        RestResponse<List<CommentResponse>> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Lấy danh sách bình luận thành công");
        response.setData(commentResponses);
        return ResponseEntity.ok(response);
    }

}
