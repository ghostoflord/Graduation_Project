package com.vn.capstone.domain.response.order;

import com.vn.capstone.util.constant.PaymentMethod;

public class PlaceOrderRequest {
    private Long userId;
    private String name;
    private String address;
    private String phone;
    private String voucherCode;

    private Long flashSaleItemId;

    private PaymentMethod paymentMethod;

    private String shippingMethod;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public Long getFlashSaleItemId() {
        return flashSaleItemId;
    }

    public void setFlashSaleItemId(Long flashSaleItemId) {
        this.flashSaleItemId = flashSaleItemId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

}
