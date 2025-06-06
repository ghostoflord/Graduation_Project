package com.vn.capstone.domain.response.voucher;

import java.time.LocalDateTime;

public class VoucherDTO {
    private Long id;
    private String code;
    private String description;
    private int discountValue;
    private boolean percentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private AssignedUserDTO assignedUser;

    // Getter/setter

    public static class AssignedUserDTO {
        private Long id;
        private String name;
        private String email;

        // Constructors
        public AssignedUserDTO(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        // Getter/setter
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    // Getter/setter cho VoucherDTO
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
        return percentage;
    }

    public void setPercentage(boolean percentage) {
        this.percentage = percentage;
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

    public AssignedUserDTO getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(AssignedUserDTO assignedUser) {
        this.assignedUser = assignedUser;
    }
}
