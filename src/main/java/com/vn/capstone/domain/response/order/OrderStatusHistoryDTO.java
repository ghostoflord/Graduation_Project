package com.vn.capstone.domain.response.order;

import java.time.Instant;

import com.vn.capstone.util.constant.OrderStatus;

public class OrderStatusHistoryDTO {
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private Instant changedAt;

    public OrderStatusHistoryDTO(OrderStatus oldStatus, OrderStatus newStatus, Instant changedAt) {
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
    }

    public OrderStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(OrderStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(OrderStatus newStatus) {
        this.newStatus = newStatus;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    

    // getters/setters
}
