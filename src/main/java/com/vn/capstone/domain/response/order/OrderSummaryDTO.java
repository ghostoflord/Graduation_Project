package com.vn.capstone.domain.response.order;

import com.vn.capstone.util.constant.OrderStatus;

public class OrderSummaryDTO {
    private long id;

    
    private double totalPrice;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private OrderStatus status;
    private long userId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }



    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
