package com.vn.capstone.util.constant;

public enum PaymentStatus {
    UNPAID, PAID, REFUNDED;

    public static PaymentStatus fromVnpStatus(String vnpStatusCode) {
        switch (vnpStatusCode) {
            case "00": // Thành công
                return PAID;
            case "01": // Thất bại hoặc bị huỷ
            case "02":
            case "24":
                return UNPAID;
            case "03": // Giả sử bạn hỗ trợ hoàn tiền từ VNPay
                return REFUNDED;
            default:
                return UNPAID; // Trạng thái mặc định nếu không rõ
        }
    }
}
