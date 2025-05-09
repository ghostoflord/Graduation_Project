package com.vn.capstone.mapping;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.response.order.OrderHistoryDTO;
import com.vn.capstone.domain.response.order.OrderItemDTO;

@Component
public class OrderMapper {
    public OrderHistoryDTO toOrderHistoryDTO(Order order) {
        OrderHistoryDTO dto = new OrderHistoryDTO();
        dto.setOrderId(order.getId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverAddress(order.getReceiverAddress());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setStatus(order.getStatus().name());
        dto.setPaymentMethod(
                order.getPaymentMethod() != null ? order.getPaymentMethod().name() : "COD");

        dto.setPaymentStatus(
                order.getPaymentStatus() != null ? order.getPaymentStatus().name() : "UNPAID");
        dto.setTrackingCode(order.getTrackingCode());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setDeliveredAt(order.getDeliveredAt());

        List<OrderItemDTO> itemDTOs = order.getOrderDetails().stream().map(detail -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setProductId(detail.getProduct().getId());
            itemDTO.setProductName(
                    detail.getProductNameSnapshot() != null ? detail.getProductNameSnapshot()
                            : (detail.getProduct() != null ? detail.getProduct().getName() : null));

            itemDTO.setProductImage(
                    detail.getProductImageSnapshot() != null ? detail.getProductImageSnapshot()
                            : (detail.getProduct() != null ? detail.getProduct().getImage() : null));
            itemDTO.setQuantity(detail.getQuantity());
            itemDTO.setPrice(detail.getPrice());
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }

}
