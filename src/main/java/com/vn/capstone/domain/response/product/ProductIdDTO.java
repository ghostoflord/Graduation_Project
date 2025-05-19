package com.vn.capstone.domain.response.product;

import com.vn.capstone.domain.ProductDetail;

import lombok.Data;

@Data
public class ProductIdDTO {
    private Long id;
    private String cpu;
    private String ram;
    private String storage;
    private String gpu;
    private String screen;
    private String battery;
    private String weight;
    private String material;
    private String os;
    private String specialFeatures;
    private String ports;
    private Long productId;

    // Constructor mapping từ entity
    public ProductIdDTO(ProductDetail detail) {
        this.id = detail.getId();
        this.cpu = detail.getCpu();
        this.ram = detail.getRam();
        this.storage = detail.getStorage();
        this.gpu = detail.getGpu();
        this.screen = detail.getScreen();
        this.battery = detail.getBattery();
        this.weight = detail.getWeight();
        this.material = detail.getMaterial();
        this.os = detail.getOs();
        this.specialFeatures = detail.getSpecialFeatures();
        this.ports = detail.getPorts();
        this.productId = detail.getProduct() != null ? detail.getProduct().getId() : null;
    }

    // Getter & Setter
}
