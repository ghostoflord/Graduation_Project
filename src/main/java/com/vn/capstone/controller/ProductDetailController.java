package com.vn.capstone.controller;

import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.ProductDetail;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.product.ProductIdDTO;
import com.vn.capstone.domain.response.productdetail.ProductDetailUpdateDTO;
import com.vn.capstone.repository.ProductDetailRepository;
import com.vn.capstone.repository.ProductRepository;
import com.vn.capstone.service.ProductDetailService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ProductDetailController {

    private final ProductDetailService productDetailService;
    private final ProductRepository productRepo;
    private final ProductDetailRepository detailRepo;

    public ProductDetailController(ProductDetailService productDetailService, ProductRepository productRepo,
            ProductDetailRepository detailRepo) {
        this.productDetailService = productDetailService;
        this.productRepo = productRepo;
        this.detailRepo = detailRepo;
    }

    @PostMapping("/product-details")
    public ResponseEntity<RestResponse<ProductIdDTO>> create(@RequestBody ProductIdDTO dto) {
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductDetail detail = new ProductDetail();
        detail.setCpu(dto.getCpu());
        detail.setRam(dto.getRam());
        detail.setStorage(dto.getStorage());
        detail.setGpu(dto.getGpu());
        detail.setScreen(dto.getScreen());
        detail.setBattery(dto.getBattery());
        detail.setWeight(dto.getWeight());
        detail.setMaterial(dto.getMaterial());
        detail.setOs(dto.getOs());
        detail.setSpecialFeatures(dto.getSpecialFeatures());
        detail.setPorts(dto.getPorts());
        detail.setProduct(product); // gắn Product entity từ productId

        ProductDetail createdDetail = detailRepo.save(detail);

        RestResponse<ProductIdDTO> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setError(null);
        response.setMessage("Tạo ProductDetail thành công");
        response.setData(new ProductIdDTO(createdDetail)); // trả DTO có productId

        return ResponseEntity.ok(response);
    }

    @PutMapping("/product-details/{id}")
    public ResponseEntity<RestResponse<ProductDetail>> update(
            @PathVariable Long id,
            @RequestBody ProductDetailUpdateDTO detailDTO) {

        ProductDetail updated = productDetailService.update(id, detailDTO);

        RestResponse<ProductDetail> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Cập nhật chi tiết sản phẩm thành công");
        response.setData(updated);
        response.setError(null);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/product-details/{id}")
    public ResponseEntity<RestResponse<Void>> delete(@PathVariable Long id) {
        RestResponse<Void> response = new RestResponse<>();
        try {
            productDetailService.delete(id);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Xóa chi tiết sản phẩm thành công");
            response.setData(null);
            response.setError(null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Xóa chi tiết sản phẩm thất bại");
            response.setError(e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/product-details")
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

    @GetMapping("/product-details/by-product/{productId}")
    public RestResponse<ProductDetail> getByProductId(@PathVariable Long productId) {
        RestResponse<ProductDetail> response = new RestResponse<>();
        try {
            ProductDetail detail = productDetailService.getByProductId(productId);
            response.setStatusCode(200);
            response.setData(detail);
            response.setMessage("Success");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("Error");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GetMapping("/product-details/{id}")
    public RestResponse<ProductDetail> getById(@PathVariable Long id) {
        RestResponse<ProductDetail> response = new RestResponse<>();
        try {
            ProductDetail detail = productDetailService.getById(id);
            response.setStatusCode(200);
            response.setData(detail);
            response.setMessage("Success");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("Error");
            response.setMessage(e.getMessage());
        }
        return response;
    }

}