package com.vn.capstone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.*;
import com.vn.capstone.domain.response.SimplifiedCartDetailDTO;
import com.vn.capstone.domain.response.cart.CartSummaryDTO;
import com.vn.capstone.service.*;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Lấy giỏ hàng của người dùng
    @GetMapping("/{userId}")
    public CartSummaryDTO getCartByUser(@PathVariable Long userId) {
        return cartService.getCartSummaryByUserId(userId);
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/add-product-json")
    public SimplifiedCartDetailDTO addProductToCartJson(@RequestBody AddToCartRequest request) {
        Product product = new Product();
        product.setId(request.getProductId());
        CartDetail cartDetail = cartService.addCartDetail(
                request.getUserId(),
                product,
                request.getQuantity(),
                request.getPrice());
        return convertCartDetailToDTO(cartDetail);
    }

    // delete sản phẩm trong giỏ hàng
    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId); // gọi service
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    private SimplifiedCartDetailDTO convertCartDetailToDTO(CartDetail cartDetail) {
        SimplifiedCartDetailDTO dto = new SimplifiedCartDetailDTO();
        dto.setId(cartDetail.getId());
        dto.setQuantity(cartDetail.getQuantity());
        dto.setPrice(cartDetail.getPrice());
        dto.setCartId(cartDetail.getCart().getId());
        dto.setUserId(cartDetail.getCart().getUser().getId());
        return dto;
    }

    // Định nghĩa lớp request để nhận dữ liệu dưới dạng JSON
    public static class AddToCartRequest {
        private Long userId;
        private Long productId;
        private long quantity;
        private double price;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}