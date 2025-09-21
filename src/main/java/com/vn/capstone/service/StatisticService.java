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

    public Long getTotalProductsByDay(LocalDate date) {
        Instant start = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return orderDetailRepository.getTotalProductsByTimeRange(start, end, OrderStatus.DELIVERED);
    }

    public Long getTotalProductsByWeek(int year, int week) {
        LocalDate startOfWeek = LocalDate.ofYearDay(year, 1)
                .with(java.time.temporal.WeekFields.ISO.weekOfYear(), week)
                .with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(7);
        Instant start = startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = endOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return orderDetailRepository.getTotalProductsByTimeRange(start, end, OrderStatus.DELIVERED);
    }

    public Long getTotalProductsByMonth(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1);
        Instant start = startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = endOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return orderDetailRepository.getTotalProductsByTimeRange(start, end, OrderStatus.DELIVERED);
    }

    public List<ProductStatisticDTO> getTotalProductsByYear(int year) {
        List<ProductStatisticDTO> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            LocalDate startOfMonth = LocalDate.of(year, month, 1);
            LocalDate endOfMonth = startOfMonth.plusMonths(1);
            Instant start = startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant end = endOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant();

            Long total = orderDetailRepository.getTotalProductsByTimeRange(start, end, OrderStatus.DELIVERED);
            if (total == null) {
                total = 0L; // tránh null
            }

            // Trả về DTO cho từng tháng
            result.add(new ProductStatisticDTO(null, null, total, month, null, null));
        }
        return result;
    }

}