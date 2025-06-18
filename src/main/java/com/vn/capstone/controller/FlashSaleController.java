package com.vn.capstone.controller;

import com.vn.capstone.domain.request.FlashSaleRequest;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.flashsale.FlashSaleDTO;
import com.vn.capstone.domain.response.flashsale.FlashSaleItemDTO;
import com.vn.capstone.service.FlashSaleService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flash-sales")
public class FlashSaleController {

    private final FlashSaleService flashSaleService;

    public FlashSaleController(FlashSaleService flashSaleService) {
        this.flashSaleService = flashSaleService;
    }

    @GetMapping("/active-items")
    public ResponseEntity<RestResponse<List<FlashSaleItemDTO>>> getActiveFlashSaleItems() {
        List<FlashSaleItemDTO> items = flashSaleService.getActiveFlashSaleItems();
        RestResponse<List<FlashSaleItemDTO>> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setError(null);
        response.setMessage("Lấy flash sale đang diễn ra thành công");
        response.setData(items);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<RestResponse<List<FlashSaleDTO>>> getAllFlashSales() {
        List<FlashSaleDTO> sales = flashSaleService.getAllFlashSales();
        RestResponse<List<FlashSaleDTO>> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setError(null);
        response.setMessage("Lấy tất cả flash sale thành công");
        response.setData(sales);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RestResponse<FlashSaleDTO>> createFlashSale(@RequestBody FlashSaleRequest request) {
        FlashSaleDTO created = flashSaleService.createFlashSale(request);
        RestResponse<FlashSaleDTO> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setError(null);
        response.setMessage("Tạo Flash Sale thành công");
        response.setData(created);
        return ResponseEntity.ok(response);
    }
}
