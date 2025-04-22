package com.vn.capstone.controller;

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
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.service.ProductService;
import com.vn.capstone.util.annotation.ApiMessage;
import com.vn.capstone.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

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
    @ApiMessage("fetch Product by id")
    public ResponseEntity<Product> getProductById(@PathVariable("id") long id) {
        Product fetchProduct = this.productService.fetchProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body(fetchProduct);
    }

    @PostMapping("/products")
    @ApiMessage("Create a new Product")
    public ResponseEntity<RestResponse<Product>> createProduct(@Valid @RequestBody Product takeProduct) {
        Product pressProduct = this.productService.handleCreateProduct(takeProduct);

        RestResponse<Product> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Tạo sản phẩm thành công");
        response.setData(pressProduct);
        response.setError(null); // hoặc bỏ qua nếu không có lỗi

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

}
