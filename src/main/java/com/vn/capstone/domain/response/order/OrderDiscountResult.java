package com.vn.capstone.domain.response.order;

public class OrderDiscountResult {
    private long discountAmount;
    private long finalAmount;

    public OrderDiscountResult() {
    }

    public OrderDiscountResult(long discountAmount, long finalAmount) {
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
    }

    public long getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(long discountAmount) {
        this.discountAmount = discountAmount;
    }

    public long getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(long finalAmount) {
        this.finalAmount = finalAmount;
    }

}
