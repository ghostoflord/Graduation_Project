package com.vn.capstone.domain.response.product;

import java.time.LocalDate;

public class ProductStatisticDTO {
    private Long productId;
    private String productName;
    private Long totalQuantity;
    private Integer month; // khi thống kê theo tháng
    private Integer week; // khi thống kê theo tuần
    private LocalDate date; // khi thống kê theo ngày

    public ProductStatisticDTO(Long productId, String productName, Long totalQuantity, Integer month, Integer week,
            LocalDate date) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.month = month;
        this.week = week;
        this.date = date;
    }

    // Constructor cho JPQL query (3 tham số)
    public ProductStatisticDTO(Long productId, String productName, Long totalQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getWeek() {
        return week;
    }

    public LocalDate getDate() {
        return date;
    }

}