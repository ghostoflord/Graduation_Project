package com.vn.capstone.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.vn.capstone.domain.FlashSale;
import com.vn.capstone.domain.FlashSaleItem;
import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.request.FlashSaleRequest;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.domain.response.flashsale.FlashSaleDTO;
import com.vn.capstone.domain.response.flashsale.FlashSaleItemDTO;
import com.vn.capstone.domain.response.flashsale.FlashSaleUpdateDTO;
import com.vn.capstone.repository.FlashSaleItemRepository;
import com.vn.capstone.repository.FlashSaleRepository;
import com.vn.capstone.repository.ProductRepository;
import com.vn.capstone.util.error.NotFoundException;

import jakarta.transaction.Transactional;

@Service
public class FlashSaleService {

    private final FlashSaleRepository flashSaleRepo;
    private final FlashSaleItemRepository flashSaleItemRepo;
    private final ProductRepository productRepository;

    public FlashSaleService(FlashSaleRepository flashSaleRepo, FlashSaleItemRepository flashSaleItemRepo,
            ProductRepository productRepository) {
        this.flashSaleRepo = flashSaleRepo;
        this.flashSaleItemRepo = flashSaleItemRepo;
        this.productRepository = productRepository;
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

    public ResultPaginationDTO fetchAllFlashSales(Specification<FlashSale> spec, Pageable pageable) {
        Page<FlashSale> flashSalePage = flashSaleRepo.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(flashSalePage.getTotalPages());
        meta.setTotal(flashSalePage.getTotalElements());

        result.setMeta(meta);

        List<FlashSaleDTO> flashSaleDTOs = flashSalePage.getContent()
                .stream()
                .map(this::convertToFlashSaleDTO)
                .collect(Collectors.toList());

        result.setResult(flashSaleDTOs);
        return result;
    }

    public FlashSaleDTO convertToFlashSaleDTO(FlashSale entity) {
        FlashSaleDTO dto = new FlashSaleDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        String status = entity.getStatus();
        dto.setStatus(status != null ? status.trim() : null);

        List<FlashSaleItemDTO> items = entity.getItems().stream().map(item -> {
            FlashSaleItemDTO itemDTO = new FlashSaleItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            String priceStr = item.getProduct().getPrice();
            Double originalPrice = null;
            try {
                originalPrice = (priceStr != null) ? Double.valueOf(priceStr) : null;
            } catch (NumberFormatException e) {
                originalPrice = 0.0; // hoặc throw/log tuỳ bạn
            }
            itemDTO.setOriginalPrice(originalPrice);

            itemDTO.setSalePrice(item.getSalePrice());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setImageUrl(item.getProduct().getImage());
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(items);
        return dto;
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // chạy mỗi 60s
    public void updateFlashSaleStatuses() {
        List<FlashSale> all = flashSaleRepo.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (FlashSale sale : all) {
            String newStatus;

            if (now.isBefore(sale.getStartTime())) {
                newStatus = "UPCOMING";
            } else if (now.isAfter(sale.getEndTime())) {
                newStatus = "ENDED";

                if (sale.getItems() != null && !sale.getItems().isEmpty()) {
                    flashSaleItemRepo.deleteAll(sale.getItems());
                    sale.getItems().clear();
                }
            } else {
                newStatus = "ACTIVE";
            }

            // Chỉ update nếu status thay đổi
            if (!newStatus.equals(sale.getStatus())) {
                sale.setStatus(newStatus);
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
                item.getQuantity(),
                item.getImageUrl());
    }

    public Optional<FlashSaleItem> getActiveFlashSaleItemForProduct(Long productId) {
        return flashSaleItemRepo.findActiveFlashSaleItemForProduct(productId, LocalDateTime.now());
    }

    // update
    @Transactional
    public void updateFlashSale(Long id, FlashSaleUpdateDTO dto) {
        FlashSale flashSale = flashSaleRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy Flash Sale"));

        // Cập nhật các field cơ bản
        flashSale.setName(dto.getName());
        flashSale.setStartTime(dto.getStartTime());
        flashSale.setEndTime(dto.getEndTime());

        if (dto.getStatus() != null) {
            flashSale.setStatus(dto.getStatus().trim());
        }

        // Xóa item cũ & thêm item mới
        flashSale.getItems().clear();

        List<FlashSaleItem> newItems = dto.getItems().stream()
                .map(itemDto -> {
                    Product product = productRepository.findById(itemDto.getProductId())
                            .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));

                    FlashSaleItem item = new FlashSaleItem();
                    item.setProduct(product);
                    item.setSalePrice(itemDto.getSalePrice());
                    item.setQuantity(itemDto.getQuantity());
                    item.setFlashSale(flashSale);
                    return item;
                })
                .collect(Collectors.toList());

        flashSale.getItems().addAll(newItems);

        flashSaleRepo.saveAndFlush(flashSale);
    }

    @Transactional
    public void deleteFlashSale(Long id) {
        FlashSale flashSale = flashSaleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Flash Sale với ID: " + id));

        // Nếu có item thì xóa từng cái trước
        List<FlashSaleItem> items = flashSale.getItems();
        if (items != null && !items.isEmpty()) {
            flashSaleItemRepo.deleteAll(items);
        }

        // Rồi xóa Flash Sale
        flashSaleRepo.delete(flashSale);
    }

    // đếm sản phẩm trong flash sale
    @Transactional
    public void reduceFlashSaleItemQuantity(Long flashSaleItemId, int quantityToReduce) {
        FlashSaleItem item = flashSaleItemRepo.findById(flashSaleItemId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm trong Flash Sale"));

        if (item.getQuantity() < quantityToReduce) {
            throw new RuntimeException("Sản phẩm không đủ số lượng khuyến mãi");
        }

        int updatedQuantity = item.getQuantity() - quantityToReduce;

        if (updatedQuantity <= 0) {
            flashSaleItemRepo.delete(item); // Xoá luôn khỏi flash sale nếu số lượng = 0
        } else {
            item.setQuantity(updatedQuantity);
            flashSaleItemRepo.save(item); // Cập nhật số lượng còn lại
        }
    }

}
