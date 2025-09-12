package com.vn.capstone.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.Slide;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.service.SlideService;
import com.vn.capstone.util.constant.SlideType;

@RestController
@RequestMapping("/api/v1")
public class SlideController {

    private final SlideService slideService;

    public SlideController(SlideService slideService) {
        this.slideService = slideService;
    }

    @GetMapping("/slides")
    public ResponseEntity<List<Slide>> getAllSlides() {
        return ResponseEntity.ok(slideService.getAllSlides());
    }

    // Lấy slide theo type (ví dụ: HOME)
    @GetMapping("/slides/type/{type}")
    public ResponseEntity<RestResponse<List<Slide>>> getSlidesByType(@PathVariable SlideType type) {
        RestResponse<List<Slide>> response = new RestResponse<>();
        try {
            List<Slide> slides = slideService.getSlidesByType(type);
            response.setStatusCode(200);
            response.setMessage("Lấy danh sách slide theo type thành công");
            response.setData(slides);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("Lỗi lấy danh sách slide");
            response.setMessage(e.getMessage());
            response.setData(null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/slides")
    public ResponseEntity<RestResponse<Slide>> createSlide(@RequestBody Slide slide) {
        RestResponse<Slide> response = new RestResponse<>();
        try {
            Slide createdSlide = slideService.createSlide(slide);
            response.setStatusCode(201);
            response.setMessage("Tạo slide thành công");
            response.setData(createdSlide);
            return ResponseEntity.status(201).body(response); // 201 Created
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("Lỗi tạo slide");
            response.setMessage(e.getMessage());
            response.setData(null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/slides/{id}")
    public ResponseEntity<RestResponse<Slide>> updateSlide(@PathVariable Long id, @RequestBody Slide slide) {
        RestResponse<Slide> response = new RestResponse<>();
        try {
            Slide updatedSlide = slideService.updateSlide(id, slide);
            response.setStatusCode(200);
            response.setMessage("Cập nhật slide thành công");
            response.setData(updatedSlide);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("Lỗi cập nhật slide");
            response.setMessage(e.getMessage());
            response.setData(null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/slides/{id}")
    public ResponseEntity<RestResponse<Void>> deleteSlide(@PathVariable Long id) {
        RestResponse<Void> response = new RestResponse<>();
        try {
            slideService.deleteSlide(id);
            response.setStatusCode(200);
            response.setMessage("Xóa slide thành công");
            response.setData(null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("Lỗi xóa slide");
            response.setMessage(e.getMessage());
            response.setData(null);
            return ResponseEntity.status(500).body(response);
        }
    }

}
