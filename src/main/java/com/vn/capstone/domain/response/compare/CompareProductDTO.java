package com.vn.capstone.domain.response.compare;

import lombok.Data;

@Data
public class CompareProductDTO {
    private Long id;
    private String name;
    private String image;

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
}
