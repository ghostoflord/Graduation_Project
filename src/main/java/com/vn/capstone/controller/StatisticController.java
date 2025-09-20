package com.vn.capstone.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.response.product.ProductStatisticDTO;
import com.vn.capstone.service.StatisticService;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticController {

    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping("/top-products/day")
    public List<ProductStatisticDTO> getTopProductsByDay(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return statisticService.getTopProductsByDay(localDate);
    }

    @GetMapping("/top-products/week")
    public List<ProductStatisticDTO> getTopProductsByWeek(@RequestParam int year, @RequestParam int week) {
        return statisticService.getTopProductsByWeek(year, week);
    }

    @GetMapping("/top-products/month")
    public List<ProductStatisticDTO> getTopProductsByMonth(@RequestParam int year, @RequestParam int month) {
        return statisticService.getTopProductsByMonth(year, month);
    }
}