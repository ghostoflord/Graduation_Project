package com.vn.capstone.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vn.capstone.domain.response.product.ProductStatisticDTO;
import com.vn.capstone.repository.OrderDetailRepository;
import com.vn.capstone.util.constant.OrderStatus;

@Service
public class StatisticService {

    private final OrderDetailRepository orderDetailRepository;

    public StatisticService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    public List<ProductStatisticDTO> getTotalProductsByYear(int year) {
        List<ProductStatisticDTO> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Long total = orderDetailRepository.getTotalProductsByMonth(year, month, OrderStatus.DELIVERED);
            if (total == null)
                total = 0L;
            result.add(new ProductStatisticDTO(null, null, total, month, null, null));
        }
        return result;
    }

}