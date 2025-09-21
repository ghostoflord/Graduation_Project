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

    // @GetMapping("/top-products/month")
    // public ProductStatisticDTO getTotalProductsByMonth(@RequestParam int year,
    // @RequestParam int month) {
    // Long total = statisticService.getTotalProductsByMonth(year, month);
    // return new ProductStatisticDTO(null, "TOTAL", total);
    // }

    @GetMapping("/top-products/year")
    public List<ProductStatisticDTO> getTotalProductsByYear(@RequestParam int year) {
        return statisticService.getTotalProductsByYear(year);
    }

}