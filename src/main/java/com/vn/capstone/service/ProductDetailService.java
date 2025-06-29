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
        ProductDetail existing = detailRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductDetail not found"));

        existing.setCpu(dto.getCpu());
        existing.setRam(dto.getRam());
        existing.setStorage(dto.getStorage());
        existing.setGpu(dto.getGpu());
        existing.setScreen(dto.getScreen());
        existing.setBattery(dto.getBattery());
        existing.setWeight(dto.getWeight());
        existing.setMaterial(dto.getMaterial());
        existing.setOs(dto.getOs());
        existing.setSpecialFeatures(dto.getSpecialFeatures());
        existing.setPorts(dto.getPorts());

        return detailRepo.save(existing);
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
