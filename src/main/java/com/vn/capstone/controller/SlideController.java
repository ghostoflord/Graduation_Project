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
    public ResponseEntity<List<Slide>> getSlidesByType(@PathVariable SlideType type) {
        return ResponseEntity.ok(slideService.getSlidesByType(type));
    }

    @PostMapping("/slides")
    public ResponseEntity<Slide> createSlide(@RequestBody Slide slide) {
        return ResponseEntity.ok(slideService.createSlide(slide));
    }

    @PutMapping("/slides/{id}")
    public ResponseEntity<Slide> updateSlide(@PathVariable Long id, @RequestBody Slide slide) {
        return ResponseEntity.ok(slideService.updateSlide(id, slide));
    }

    @DeleteMapping("/slides/{id}")
    public ResponseEntity<Void> deleteSlide(@PathVariable Long id) {
        slideService.deleteSlide(id);
        return ResponseEntity.noContent().build();
    }
}
