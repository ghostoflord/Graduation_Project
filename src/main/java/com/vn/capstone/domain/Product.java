package com.vn.capstone.domain;

import com.vn.capstone.util.constant.GuaranteeEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String productCode;
    private String detailDescription;
    // in_stock,out_of_stock
    @Enumerated(EnumType.STRING)
    private GuaranteeEnum guarantee;
    private String image;

    private String factory;
    private String price;
    private String sold;
    private String quantity;
    private String shortDescription;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getDetailDescription() {
        return detailDescription;
    }

    public void setDetailDescription(String detailDescription) {
        this.detailDescription = detailDescription;
    }

    public GuaranteeEnum getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(GuaranteeEnum guarantee) {
        this.guarantee = guarantee;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    @Override
    public String toString() {
        return "Product [name=" + name + ", productCode=" + productCode + ", detailDescription=" + detailDescription
                + ", guarantee=" + guarantee + ", image=" + image + ", factory=" + factory + ", price=" + price
                + ", sold=" + sold + ", quantity=" + quantity + ", shortDescription=" + shortDescription + "]";
    }
}
