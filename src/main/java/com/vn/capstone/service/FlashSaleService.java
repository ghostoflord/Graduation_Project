package com.vn.capstone.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.vn.capstone.domain.FlashSale;
import com.vn.capstone.domain.FlashSaleItem;
import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.request.FlashSaleRequest;
import com.vn.capstone.domain.response.flashsale.FlashSaleDTO;
import com.vn.capstone.domain.response.flashsale.FlashSaleItemDTO;
import com.vn.capstone.repository.FlashSaleItemRepository;
import com.vn.capstone.repository.FlashSaleRepository;

@Service
public class FlashSaleService {

    private final FlashSaleRepository flashSaleRepo;
    private final FlashSaleItemRepository flashSaleItemRepo;

    public FlashSaleService(FlashSaleRepository flashSaleRepo, FlashSaleItemRepository flashSaleItemRepo) {
        this.flashSaleRepo = flashSaleRepo;
        this.flashSaleItemRepo = flashSaleItemRepo;
    }

    public List<FlashSaleItemDTO> getActiveFlashSaleItems() {
        List<FlashSale> activeSales = flashSaleRepo.findByStatus("ACTIVE");
        if (activeSales.isEmpty())
            return Collections.emptyList();

        return flashSaleItemRepo.findByFlashSaleId(activeSales.get(0).getId())
                .stream()
                .map(this::toFlashSaleItemDTO)
                .collect(Collectors.toList());
    }

    public List<FlashSaleDTO> getAllFlashSales() {
        return flashSaleRepo.findAll().stream()
                .map(this::toFlashSaleDTO)
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 60000)
    public void updateFlashSaleStatuses() {
        List<FlashSale> all = flashSaleRepo.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (FlashSale sale : all) {
            if (now.isBefore(sale.getStartTime())) {
                sale.setStatus("UPCOMING");
            } else if (now.isAfter(sale.getEndTime())) {
                sale.setStatus("ENDED");
            } else {
                sale.setStatus("ACTIVE");
            }
        }
        flashSaleRepo.saveAll(all);
    }

    public FlashSaleDTO createFlashSale(FlashSaleRequest request) {
        FlashSale sale = new FlashSale();
        sale.setName(request.getName());
        sale.setStartTime(request.getStartTime());
        sale.setEndTime(request.getEndTime());
        sale.setStatus("UPCOMING");

        List<FlashSaleItem> items = new ArrayList<>();
        for (FlashSaleRequest.FlashSaleItemDTO dto : request.getItems()) {
            FlashSaleItem item = new FlashSaleItem();
            item.setFlashSale(sale);

            Product product = new Product();
            product.setId(dto.getProductId());
            item.setProduct(product);

            item.setSalePrice(dto.getSalePrice());
            item.setOriginalPrice(0.0);
            item.setQuantity(dto.getQuantity());
            items.add(item);
        }

        sale.setItems(items);
        FlashSale saved = flashSaleRepo.save(sale);
        return toFlashSaleDTO(saved);
    }

    private FlashSaleDTO toFlashSaleDTO(FlashSale flashSale) {
        List<FlashSaleItemDTO> itemDTOs = new ArrayList<>();
        for (FlashSaleItem item : flashSale.getItems()) {
            FlashSaleItemDTO dto = toFlashSaleItemDTO(item);
            itemDTOs.add(dto);
        }

        return new FlashSaleDTO(
                flashSale.getId(),
                flashSale.getName(),
                flashSale.getStartTime(),
                flashSale.getEndTime(),
                flashSale.getStatus(),
                itemDTOs);
    }

    private FlashSaleItemDTO toFlashSaleItemDTO(FlashSaleItem item) {
        return new FlashSaleItemDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getOriginalPrice(),
                item.getSalePrice(),
                item.getQuantity());
    }
}
