package com.vn.capstone.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vn.capstone.util.SecurityUtil;
import com.vn.capstone.util.constant.BestsellEnum;
import com.vn.capstone.util.constant.GuaranteeEnum;
import com.vn.capstone.util.constant.ProductCategoryEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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

    @Enumerated(EnumType.STRING)
    private BestsellEnum bestsell;

    private String factory;
    private String price;
    private String sold;
    private String quantity;
    private String shortDescription;

    private String slug;

    private String sell;

    @Enumerated(EnumType.STRING)
    private ProductCategoryEnum category;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Comment> comments;

    @OneToOne(mappedBy = "product")
    private ProductDetail productDetail;

    public ProductCategoryEnum getCategory() {
        return category;
    }

    public void setCategory(ProductCategoryEnum category) {
        this.category = category;
    }

    public ProductDetail getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(ProductDetail productDetail) {
        this.productDetail = productDetail;
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public BestsellEnum getBestsell() {
        return bestsell;
    }

    public void setBestsell(BestsellEnum bestsell) {
        this.bestsell = bestsell;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
    }

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "Product [name=" + name + ", productCode=" + productCode + ", detailDescription=" + detailDescription
                + ", guarantee=" + guarantee + ", image=" + image + ", factory=" + factory + ", price=" + price
                + ", sold=" + sold + ", quantity=" + quantity + ", shortDescription=" + shortDescription
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", createdBy=" + createdBy + ", updatedBy="
                + updatedBy + "]";
    }

}
