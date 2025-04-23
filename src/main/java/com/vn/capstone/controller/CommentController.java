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
import com.vn.capstone.service.CommentService;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentRequest request) {
        Comment comment = commentService.saveComment(request.getUserId(), request.getProductId(), request.getContent());
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Comment>> getCommentsByProduct(@PathVariable Long productId) {
        List<Comment> comments = commentService.getCommentsByProduct(productId);
        return ResponseEntity.ok(comments);
    }
}
