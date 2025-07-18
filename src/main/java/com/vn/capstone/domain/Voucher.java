package com.vn.capstone.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code; // mã ví dụ: SALE10
    private String description;

    private int discountValue; // 10,000 hoặc 10 (nếu là %)
    private boolean isPercentage; // true: 10%, false: 10k

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean isSingleUse; // dùng 1 lần

    private boolean isActive = true;

    // Optional: Chỉ user cụ thể mới dùng được
    @ManyToOne
    private User assignedUser;

    private boolean used = false; // nếu single-use thì đánh dấu đã dùng

    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "voucher")
    @JsonIgnore
    private List<UserVoucher> userVouchers;

    @ManyToOne
    private Product applicableProduct;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(int discountValue) {
        this.discountValue = discountValue;
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public void setPercentage(boolean isPercentage) {
        this.isPercentage = isPercentage;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isSingleUse() {
        return isSingleUse;
    }

    public void setSingleUse(boolean isSingleUse) {
        this.isSingleUse = isSingleUse;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
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

    public List<UserVoucher> getUserVouchers() {
        return userVouchers;
    }

    public void setUserVouchers(List<UserVoucher> userVouchers) {
        this.userVouchers = userVouchers;
    }

    public Product getApplicableProduct() {
        return applicableProduct;
    }

    public void setApplicableProduct(Product applicableProduct) {
        this.applicableProduct = applicableProduct;
    }

}
