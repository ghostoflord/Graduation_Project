package com.vn.capstone.service;

import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.ProductDetail;
import com.vn.capstone.repository.ProductDetailRepository;
import com.vn.capstone.repository.ProductRepository;
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

    public ProductDetail update(Long id, ProductDetail detail) {
        ProductDetail existing = detailRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductDetail not found"));

        existing.setCpu(detail.getCpu());
        existing.setRam(detail.getRam());
        existing.setStorage(detail.getStorage());
        existing.setGpu(detail.getGpu());
        existing.setScreen(detail.getScreen());
        existing.setBattery(detail.getBattery());
        existing.setWeight(detail.getWeight());
        existing.setMaterial(detail.getMaterial());
        existing.setOs(detail.getOs());
        existing.setSpecialFeatures(detail.getSpecialFeatures());
        existing.setPorts(detail.getPorts());

        return detailRepo.save(existing);
    }

    public void delete(Long id) {
        detailRepo.deleteById(id);
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
