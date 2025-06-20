package com.vn.capstone.controller;

import com.turkraft.springfilter.boot.Filter;
import com.vn.capstone.domain.FlashSale;
import com.vn.capstone.domain.request.FlashSaleRequest;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.domain.response.flashsale.FlashSaleDTO;
import com.vn.capstone.domain.response.flashsale.FlashSaleItemDTO;
import com.vn.capstone.service.FlashSaleService;
import com.vn.capstone.util.annotation.ApiMessage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
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
    @ApiMessage("Fetch all flash sales")
    public ResponseEntity<RestResponse<ResultPaginationDTO>> getAllFlashSales(
            @Filter Specification<FlashSale> spec,
            Pageable pageable) {
        ResultPaginationDTO result = flashSaleService.fetchAllFlashSales(spec, pageable);

        RestResponse<ResultPaginationDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Fetch all flash sales successfully");
        response.setData(result);

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
