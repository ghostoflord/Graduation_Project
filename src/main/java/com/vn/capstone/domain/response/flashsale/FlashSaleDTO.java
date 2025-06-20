package com.vn.capstone.domain.response.flashsale;

import java.time.LocalDateTime;
import java.util.List;

public class FlashSaleDTO {
    private Long id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private List<FlashSaleItemDTO> items;

    public FlashSaleDTO() {
    }

    public FlashSaleDTO(Long id, String name, LocalDateTime startTime, LocalDateTime endTime, String status,
            List<FlashSaleItemDTO> items) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.items = items;
    }

    // Getter, Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status != null) ? status.trim() : null;
    }

    public List<FlashSaleItemDTO> getItems() {
        return items;
    }

    public void setItems(List<FlashSaleItemDTO> items) {
        this.items = items;
    }
}
