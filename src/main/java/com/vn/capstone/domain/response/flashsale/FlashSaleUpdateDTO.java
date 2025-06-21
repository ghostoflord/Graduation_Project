package com.vn.capstone.domain.response.flashsale;

import java.time.LocalDateTime;
import java.util.List;

public class FlashSaleUpdateDTO {
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private List<FlashSaleItemUpdateDTO> items;

    public FlashSaleUpdateDTO() {
    }

    public FlashSaleUpdateDTO(String name, LocalDateTime startTime, LocalDateTime endTime, String status,
            List<FlashSaleItemUpdateDTO> items) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<FlashSaleItemUpdateDTO> getItems() {
        return items;
    }

    public void setItems(List<FlashSaleItemUpdateDTO> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
