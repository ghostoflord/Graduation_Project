package com.vn.capstone.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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

    public List<ProductStatisticDTO> getTopProductsByDay(LocalDate date) {
        Instant start = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return orderDetailRepository.getTopProductsByTimeRange(start, end, OrderStatus.DELIVERED);
    }

    public List<ProductStatisticDTO> getTopProductsByWeek(int year, int week) {
        LocalDate startOfWeek = LocalDate.ofYearDay(year, 1)
                .with(java.time.temporal.WeekFields.ISO.weekOfYear(), week)
                .with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(7);
        Instant start = startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = endOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return orderDetailRepository.getTopProductsByTimeRange(start, end, OrderStatus.DELIVERED);
    }

    public List<ProductStatisticDTO> getTopProductsByMonth(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1);
        Instant start = startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = endOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return orderDetailRepository.getTopProductsByTimeRange(start, end, OrderStatus.DELIVERED);
    }
}