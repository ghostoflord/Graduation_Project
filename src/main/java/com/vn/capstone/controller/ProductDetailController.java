package com.vn.capstone.controller;

import com.vn.capstone.domain.ProductDetail;
import com.vn.capstone.service.ProductDetailService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public List<ProductDetail> getAll() {
        return productDetailService.getAll();
    }

    @GetMapping("/by-product/{productId}")
    public ProductDetail getByProductId(@PathVariable Long productId) {
        return productDetailService.getByProductId(productId);
    }
}
