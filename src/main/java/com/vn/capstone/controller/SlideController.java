package com.vn.capstone.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.Slide;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.product.ProductUpdateRequest;
import com.vn.capstone.domain.response.slide.CreateSlideDTO;
import com.vn.capstone.domain.response.slide.SlideUpdateRequest;
import com.vn.capstone.service.SlideService;
import com.vn.capstone.util.constant.SlideType;

@RestController
@RequestMapping("/api/v1")
public class SlideController {

    @Value("${upload.slide-dir}")
    private String slideUploadDir;

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
    public ResponseEntity<RestResponse<Slide>> createSlide(@RequestBody CreateSlideDTO dto) throws IOException {
        RestResponse<Slide> response = new RestResponse<>();

        try {
            // Decode ảnh base64 và lưu vào thư mục
            String savedImage = dto.getImageUrl() != null ? saveSlideImage(dto.getImageUrl()) : null;

            if (savedImage != null) {
                System.out.println("Slide image saved: " + savedImage);
            } else {
                System.out.println("Không có ảnh hoặc Base64 rỗng.");
            }
            // Gán dữ liệu vào entity Slide
            Slide slide = new Slide();
            slide.setTitle(dto.getTitle());
            slide.setDescription(dto.getDescription());
            slide.setImageUrl(savedImage);
            slide.setRedirectUrl(dto.getRedirectUrl());
            slide.setActive(dto.getActive() != null ? dto.getActive() : true);
            slide.setOrderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : 0);
            slide.setType(dto.getType());
            slide.setCreatedAt(Instant.now());
            slide.setUpdatedAt(Instant.now());

            // Lưu DB qua service
            Slide createdSlide = slideService.createSlide(slide);

            response.setStatusCode(HttpStatus.CREATED.value());
            response.setMessage("Tạo slide thành công");
            response.setData(createdSlide);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setError("Lỗi tạo slide");
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private String saveSlideImage(String imageBase64) throws IOException {
        if (imageBase64 == null || imageBase64.trim().isEmpty()) {
            return null;
        }
        String base64Image = imageBase64.contains(",")
                ? imageBase64.substring(imageBase64.indexOf(",") + 1)
                : imageBase64;

        try {
            byte[] decoded = Base64.getDecoder().decode(base64Image);

            String fileName = "slide_" + System.currentTimeMillis() + ".jpg";

            File uploadDir = new File(slideUploadDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fullPath = slideUploadDir + File.separator + fileName;
            try (FileOutputStream fos = new FileOutputStream(fullPath)) {
                fos.write(decoded);
            }

            return fileName;

        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @PutMapping("/slides/{id}")
    public ResponseEntity<RestResponse<Slide>> updateSlide(
            @PathVariable Long id,
            @RequestBody SlideUpdateRequest request) throws IOException {

        Slide slide = slideService.fetchSlideById(id);
        if (slide == null) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }

        // cập nhật thông tin cơ bản
        slide.setTitle(request.getTitle());
        slide.setDescription(request.getDescription());
        slide.setRedirectUrl(request.getRedirectUrl());
        slide.setActive(request.isActive());
        slide.setOrderIndex(request.getOrderIndex());
        slide.setType(request.getType());

        // xử lý ảnh nếu có
        if (request.getImageBase64() != null && !request.getImageBase64().trim().isEmpty()) {
            // xóa ảnh cũ
            if (slide.getImageUrl() != null) {
                File oldImage = new File(slideUploadDir + File.separator + slide.getImageUrl());
                if (oldImage.exists())
                    oldImage.delete();
            }
            // lưu ảnh mới
            String savedImage = saveSlideImage(request.getImageBase64());
            slide.setImageUrl(savedImage);
        }

        Slide updatedSlide = slideService.handleUpdateSlide(slide);

        RestResponse<Slide> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Cập nhật slide thành công");
        response.setData(updatedSlide);
        return ResponseEntity.ok(response);
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
