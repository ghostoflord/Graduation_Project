package com.vn.capstone.controller;

import com.vn.capstone.domain.Voucher;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.order.OrderDiscountResult;
import com.vn.capstone.domain.response.voucher.VoucherDTO;
import com.vn.capstone.domain.response.voucher.VoucherRequest;
import com.vn.capstone.service.VoucherService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @PostMapping
    public ResponseEntity<RestResponse<Voucher>> createVoucher(@RequestBody VoucherRequest voucher) {
        Voucher created = voucherService.createVoucher(voucher);
        RestResponse<Voucher> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Voucher created successfully");
        response.setData(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/assign")
    public ResponseEntity<RestResponse<String>> assignVoucher(
            @RequestParam Long voucherId,
            @RequestParam Long userId) {
        voucherService.assignVoucherToUser(voucherId, userId);
        RestResponse<String> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Voucher assigned to user successfully");
        response.setData("Success");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/apply")
    public ResponseEntity<RestResponse<OrderDiscountResult>> applyVoucher(
            @RequestParam String code,
            @RequestParam Long userId,
            @RequestParam int orderTotal) {

        OrderDiscountResult result = voucherService.applyVoucher(code, userId, orderTotal);

        RestResponse<OrderDiscountResult> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Voucher applied successfully");
        response.setData(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<RestResponse<List<VoucherDTO>>> getAllVouchers() {
        List<VoucherDTO> vouchers = voucherService.getAllVouchers();
        RestResponse<List<VoucherDTO>> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("List of all vouchers");
        response.setData(vouchers);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse<String>> deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        RestResponse<String> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Voucher deleted successfully");
        response.setData("Deleted");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<RestResponse<List<VoucherDTO>>> getVouchersForUser(@PathVariable Long userId) {
        List<VoucherDTO> vouchers = voucherService.getAvailableVouchersForUser(userId);

        RestResponse<List<VoucherDTO>> response = new RestResponse<>();
        if (vouchers.isEmpty()) {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setError("Không tìm thấy voucher nào hoặc user không tồn tại");
            response.setMessage(null);
            response.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setStatusCode(HttpStatus.OK.value());
        response.setError(null);
        response.setMessage("Danh sách voucher khả dụng cho người dùng");
        response.setData(vouchers);

        return ResponseEntity.ok(response);
    }

}
