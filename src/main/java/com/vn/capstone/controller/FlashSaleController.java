package com.vn.capstone.controller;

import com.turkraft.springfilter.boot.Filter;
import com.vn.capstone.domain.FlashSale;
import com.vn.capstone.domain.FlashSaleItem;
import com.vn.capstone.domain.request.FlashSaleRequest;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.domain.response.flashsale.FlashSaleDTO;
import com.vn.capstone.domain.response.flashsale.FlashSaleItemDTO;
import com.vn.capstone.domain.response.flashsale.FlashSaleUpdateDTO;
import com.vn.capstone.repository.FlashSaleItemRepository;
import com.vn.capstone.service.FlashSaleService;
import com.vn.capstone.util.annotation.ApiMessage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/flash-sales")
public class FlashSaleController {

    private final FlashSaleService flashSaleService;
    private final FlashSaleItemRepository flashSaleItemRepo;

    public FlashSaleController(FlashSaleService flashSaleService, FlashSaleItemRepository flashSaleItemRepo) {
        this.flashSaleService = flashSaleService;
        this.flashSaleItemRepo = flashSaleItemRepo;
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

    @PutMapping("/{id}")
    public ResponseEntity<RestResponse<Void>> updateFlashSale(
            @PathVariable Long id,
            @RequestBody FlashSaleUpdateDTO dto) {

        flashSaleService.updateFlashSale(id, dto);

        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setError(null);
        response.setMessage("Cập nhật Flash Sale thành công");
        response.setData(null);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse<Void>> deleteFlashSale(@PathVariable Long id) {
        flashSaleService.deleteFlashSale(id);

        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Xóa Flash Sale thành công");
        response.setData(null);
        response.setError(null); // không lỗi nên để null
        return ResponseEntity.ok(response);
    }

    // đếm số lượng sản phẩm flash sale
    @PostMapping("/reduce-quantity")
    public ResponseEntity<RestResponse<Void>> reduceQuantity(@RequestParam Long flashSaleItemId,
            @RequestParam int quantity) {
        flashSaleService.reduceFlashSaleItemQuantity(flashSaleItemId, quantity);

        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Quantity reduced successfully");
        response.setData(null); // Vì không trả object cụ thể
        response.setError(null);

        return ResponseEntity.ok(response);
    }

    ///
    @GetMapping("/flash-sale-items/{id}/validate")
    public ResponseEntity<RestResponse<Map<String, Object>>> validateFlashSaleItem(@PathVariable Long id) {
        FlashSaleItem item = flashSaleItemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        FlashSale flashSale = item.getFlashSale();
        LocalDateTime now = LocalDateTime.now();

        boolean expired = now.isAfter(flashSale.getEndTime());

        Map<String, Object> result = new HashMap<>();
        result.put("expired", expired);
        result.put("originalPrice", item.getProduct().getPrice());

        RestResponse<Map<String, Object>> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Validate flash sale item successfully");
        response.setData(result);

        return ResponseEntity.ok(response);
    }

}
