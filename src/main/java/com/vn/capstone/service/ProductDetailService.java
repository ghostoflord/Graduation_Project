package com.vn.capstone.service;

import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.ProductDetail;
import com.vn.capstone.domain.response.productdetail.ProductDetailUpdateDTO;
import com.vn.capstone.repository.ProductDetailRepository;
import com.vn.capstone.repository.ProductRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductDetailService {

    @Autowired
    private ProductDetailRepository detailRepo;

    @Autowired
    private ProductRepository productRepo;

    public ProductDetail create(ProductDetail detail) {
        Product product = productRepo.findById(detail.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        detail.setProduct(product);
        return detailRepo.save(detail);
    }

    public ProductDetail update(Long id, ProductDetailUpdateDTO dto) {
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Nếu product chưa có detail thì tạo mới
        ProductDetail detail = product.getProductDetail();
        if (detail == null) {
            detail = new ProductDetail();
            detail.setProduct(product);
        }

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

        return detailRepo.save(detail);
    }

    @Transactional
    public void delete(Long id) {
        ProductDetail detail = detailRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm với id = " + id));

        Product product = detail.getProduct(); // lấy product đã có

        // Gỡ productDetail khỏi product -> JPA sẽ tự động xoá do orphanRemoval = true
        product.setProductDetail(null);

        // Lưu lại product để cập nhật thay đổi
        productRepo.save(product);
    }

    public ProductDetail getById(Long id) {
        return detailRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductDetail not found"));
    }

    public List<ProductDetail> getAll() {
        return detailRepo.findAll();
    }

    public ProductDetail getByProductId(Long productId) {
        return detailRepo.findByProductId(productId);
    }
}
