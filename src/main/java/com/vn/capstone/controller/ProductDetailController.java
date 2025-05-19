package com.vn.capstone.controller;

import com.vn.capstone.domain.ProductDetail;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.product.ProductDetailDTO;
import com.vn.capstone.domain.response.product.ProductIdDTO;
import com.vn.capstone.service.ProductDetailService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/product-details")
public class ProductDetailController {

    private final ProductDetailService productDetailService;

    public ProductDetailController(ProductDetailService productDetailService) {
        this.productDetailService = productDetailService;
    }

    @PostMapping
    public ProductDetail create(@RequestBody ProductDetail detail) {
        return productDetailService.create(detail);
    }

    @PutMapping("/{id}")
    public ProductDetail update(@PathVariable Long id, @RequestBody ProductDetail detail) {
        return productDetailService.update(id, detail);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productDetailService.delete(id);
    }

    @GetMapping("/{id}")
    public ProductDetail getById(@PathVariable Long id) {
        return productDetailService.getById(id);
    }

    @GetMapping()
    public ResponseEntity<RestResponse<List<ProductIdDTO>>> getAll() {
        List<ProductDetail> list = productDetailService.getAll();

        // In ra log để kiểm tra
        for (ProductDetail detail : list) {
            Long detailId = detail.getId();
            Long productId = detail.getProduct() != null ? detail.getProduct().getId() : null;
            System.out.println("ProductDetail ID: " + detailId + ", Product ID: " + productId);
        }

        // Convert sang DTO
        List<ProductIdDTO> dtoList = list.stream()
                .map(ProductIdDTO::new)
                .collect(Collectors.toList());

        // Trả về response
        RestResponse<List<ProductIdDTO>> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setError(null);
        response.setMessage("Lấy danh sách product detail thành công");
        response.setData(dtoList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-product/{productId}")
    public ProductDetail getByProductId(@PathVariable Long productId) {
        return productDetailService.getByProductId(productId);
    }
}