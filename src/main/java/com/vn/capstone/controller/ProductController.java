package com.vn.capstone.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import java.nio.file.*;
import java.util.Base64;
import java.io.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

import com.turkraft.springfilter.boot.Filter;

import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.response.CreateProductDTO;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.domain.response.product.ProductUpdateRequest;
import com.vn.capstone.service.ProductService;
import com.vn.capstone.util.annotation.ApiMessage;
import com.vn.capstone.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    @Value("${upload.product-dir}")
    private String productUploadDir;

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    @ApiMessage("fetch all Product")
    public ResponseEntity<RestResponse<ResultPaginationDTO>> getAllProduct(
            @Filter Specification<Product> spec,
            Pageable pageable) {

        ResultPaginationDTO result = this.productService.fetchAllProduct(spec, pageable);

        RestResponse<ResultPaginationDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Lấy danh sách sản phẩm thành công");
        response.setData(result);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/products/{id}")
    @ApiMessage("Fetch Product by id")
    public ResponseEntity<RestResponse<Product>> getProductById(@PathVariable("id") long id) {
        Product fetchProduct = this.productService.fetchProductById(id);

        RestResponse<Product> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Lấy sản phẩm thành công");
        response.setData(fetchProduct);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/products/slug/{slug}")
    @ApiMessage("Fetch Product by slug")
    public ResponseEntity<RestResponse<Product>> getProductBySlug(@PathVariable("slug") String slug) {
        Product fetchProduct = productService.fetchProductBySlug(slug);

        RestResponse<Product> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Lấy sản phẩm thành công theo slug");
        response.setData(fetchProduct);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/products/slug/generate")
    @ApiMessage("Generate missing slugs for all products")
    public ResponseEntity<RestResponse<String>> generateSlugsForProducts() {
        productService.updateSlugsForExistingProducts();

        RestResponse<String> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Tạo slug tự động thành công cho các sản phẩm.");
        response.setData("OK");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/products")
    public ResponseEntity<RestResponse<Product>> createProduct(@RequestBody CreateProductDTO dto) throws IOException {
        String savedImage = dto.getImage() != null ? saveImage(dto.getImage()) : null;

        if (savedImage != null) {
            System.out.println("Image saved: " + savedImage); // Log giá trị ảnh
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setProductCode(dto.getProductCode());
        product.setDetailDescription(dto.getDetailDescription());
        product.setShortDescription(dto.getShortDescription());
        product.setGuarantee(dto.getGuarantee());
        product.setFactory(dto.getFactory());
        product.setPrice(dto.getPrice());
        product.setSold(dto.getSold());
        product.setQuantity(dto.getQuantity());
        product.setImage(savedImage);
        product.setBestsell(dto.getBestsell());
        product.setSell(dto.getSell());
        product.setImage(savedImage); // Đảm bảo gán đúng tên ảnh

        // Kiểm tra lại giá trị trước khi lưu vào cơ sở dữ liệux
        System.out.println("Saving product: " + product);

        Product savedProduct = productService.handleCreateProduct(product);

        RestResponse<Product> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Tạo sản phẩm thành công");
        response.setData(savedProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private String saveImage(String imageBase64) throws IOException {
        if (imageBase64 == null || imageBase64.trim().isEmpty()) {
            return null;
        }

        // Log lại Base64 để kiểm tra
        System.out.println("Received Base64 Image: " + imageBase64);

        // Kiểm tra và tách bỏ phần tiền tố nếu có
        String base64Image;
        if (imageBase64.contains(",")) {
            base64Image = imageBase64.substring(imageBase64.indexOf(",") + 1);
        } else {
            base64Image = imageBase64;
        }

        // Log Base64 sau khi tách tiền tố
        System.out.println("Base64 Image after stripping prefix: " + base64Image);

        try {
            // Decode base64
            byte[] decoded = Base64.getDecoder().decode(base64Image);

            // Tạo tên file duy nhất cho ảnh sản phẩm
            String fileName = "product_" + System.currentTimeMillis() + ".jpg";

            // Tạo thư mục lưu trữ nếu chưa có
            File uploadDir = new File(productUploadDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fullPath = productUploadDir + File.separator + fileName;
            try (FileOutputStream fos = new FileOutputStream(fullPath)) {
                fos.write(decoded);
            }

            return fileName; // Trả về tên file của ảnh đã lưu
        } catch (IllegalArgumentException e) {
            // Log lỗi nếu Base64 không hợp lệ
            System.err.println("Base64 decoding error: " + e.getMessage());
            return null;
        }
    }

    @DeleteMapping("/products/{id}")
    @ApiMessage("Delete a Product")
    public ResponseEntity<RestResponse<Void>> deleteProduct(@PathVariable("id") long id)
            throws IdInvalidException {
        Product currentProduct = this.productService.fetchProductById(id);
        if (currentProduct == null) {
            throw new IdInvalidException("Product with id = " + id + " does not exist");
        }

        this.productService.handleDeleteProduct(id);

        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Delete product successfully");
        response.setData(null);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/products")
    public ResponseEntity<RestResponse<Product>> updateProduct(@Valid @RequestBody Product product)
            throws IdInvalidException {
        Product pressProduct = this.productService.handleUpdateProduct(product);

        if (pressProduct == null) {
            throw new IdInvalidException("Product with id = " + product.getId() + " does not exist");
        }

        RestResponse<Product> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Cập nhật sản phẩm thành công");
        response.setData(pressProduct);
        response.setError(null);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/products/update")
    public ResponseEntity<RestResponse<Product>> updateProduct(@RequestBody ProductUpdateRequest request)
            throws IOException {

        // Tìm sản phẩm theo id
        Product product = productService.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + request.getId()));

        // Cập nhật thông tin sản phẩm
        product.setName(request.getName());
        product.setProductCode(request.getProductCode());
        product.setDetailDescription(request.getDetailDescription());
        product.setGuarantee(request.getGuarantee());
        product.setFactory(request.getFactory());
        product.setPrice(request.getPrice());
        product.setSold(request.getSold());
        product.setQuantity(request.getQuantity());
        product.setShortDescription(request.getShortDescription());
        product.setBestsell(request.getBestsell());
        product.setSell(request.getSell());

        // Nếu có ảnh mới
        if (request.getImage() != null && !request.getImage().trim().isEmpty()) {
            // Xóa ảnh cũ nếu có
            if (product.getImage() != null) {
                File oldImage = new File(productUploadDir + File.separator + product.getImage());
                if (oldImage.exists()) {
                    oldImage.delete();
                }
            }

            // Lưu ảnh mới (không cần base64 nữa, sử dụng tên file)
            String newImageFileName = request.getImage(); // Trực tiếp lấy tên file từ frontend
            product.setImage(newImageFileName);
        }

        // Cập nhật sản phẩm trong DB
        Product updatedProduct = productService.handleUpdateProduct(product);

        // Trả về response
        RestResponse<Product> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Cập nhật sản phẩm thành công");
        response.setData(updatedProduct);
        return ResponseEntity.ok(response);
    }

}
