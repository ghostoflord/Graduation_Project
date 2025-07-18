package com.vn.capstone.domain.response;

import com.vn.capstone.domain.response.product.ProductDetailDTO;
import com.vn.capstone.util.constant.BestsellEnum;
import com.vn.capstone.util.constant.GenderEnum;
import com.vn.capstone.util.constant.GuaranteeEnum;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class CreateProductDTO {
    private String name;
    private String productCode;
    private String detailDescription;
    private String shortDescription;
    @Enumerated(EnumType.STRING)
    private GuaranteeEnum guarantee;
    private String factory;
    private String price;
    private String sold;
    private String quantity;
    private String image; // base64 image

    @Enumerated(EnumType.STRING)
    private BestsellEnum bestsell;

    private String sell;

    private ProductDetailDTO detail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetailDescription() {
        return detailDescription;
    }

    public void setDetailDescription(String detailDescription) {
        this.detailDescription = detailDescription;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public BestsellEnum getBestsell() {
        return bestsell;
    }

    public void setBestsell(BestsellEnum bestsell) {
        this.bestsell = bestsell;
    }

    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
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

    public GuaranteeEnum getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(GuaranteeEnum guarantee) {
        this.guarantee = guarantee;
    }

    public ProductDetailDTO getDetail() {
        return detail;
    }

    public void setDetail(ProductDetailDTO detail) {
        this.detail = detail;
    }

}