package com.vn.capstone.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.compare.CompareProductDTO;
import com.vn.capstone.domain.response.product.ProductDetailDTO;
import com.vn.capstone.service.ProductService;

@RestController
@RequestMapping("/api/v1")
public class CompareController {

    private final ProductService productService;

    public CompareController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/compare")
    public ResponseEntity<RestResponse<List<CompareProductDTO>>> compareProducts(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());

        List<CompareProductDTO> result = productService.getProductsForComparison(idList);

        RestResponse<List<CompareProductDTO>> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setError(null);
        response.setMessage("So sánh sản phẩm thành công");
        response.setData(result);

        return ResponseEntity.ok(response);
    }

}
