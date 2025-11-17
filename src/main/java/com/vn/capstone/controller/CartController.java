package com.vn.capstone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.CartDetail;
import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.SimplifiedCartDetailDTO;
import com.vn.capstone.domain.response.cart.AddToCartRequest;
import com.vn.capstone.domain.response.cart.CartSummaryDTO;
import com.vn.capstone.repository.ProductRepository;
import com.vn.capstone.service.CartService;

@RestController
@RequestMapping("/api/v1")
public class CartController {

    private final CartService cartService;
    private final ProductRepository productRepository;

    public CartController(CartService cartService, ProductRepository productRepository) {
        this.cartService = cartService;
        this.productRepository = productRepository;
    }

    // Lấy giỏ hàng của người dùng
    @GetMapping("/carts/users/{userId}")
    public RestResponse<CartSummaryDTO> getCartByUser(@PathVariable Long userId) {
        CartSummaryDTO cartSummary = cartService.getCartSummaryByUserId(userId);

        RestResponse<CartSummaryDTO> response = new RestResponse<>();
        response.setStatusCode(200); // hoặc HttpStatus.OK.value()
        response.setError(null);
        response.setMessage("Success");
        response.setData(cartSummary);

        return response;
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/carts/addproduct")
    public RestResponse<SimplifiedCartDetailDTO> addProductToCartJson(@RequestBody AddToCartRequest request) {
        // Lấy product từ DB
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Nếu có discountPrice > 0 thì lấy discountPrice, còn không thì lấy price
        double finalPrice;
        try {
            if (product.getDiscountPrice() != null && !product.getDiscountPrice().isEmpty()
                    && Double.parseDouble(product.getDiscountPrice()) > 0) {
                finalPrice = Double.parseDouble(product.getDiscountPrice());
            } else {
                finalPrice = Double.parseDouble(product.getPrice());
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    "Giá sản phẩm không hợp lệ: " + product.getPrice() + " / " + product.getDiscountPrice());
        }

        // Gọi service addCartDetail
        CartDetail cartDetail = cartService.addCartDetail(
                request.getUserId(),
                product,
                request.getQuantity(),
                finalPrice);

        SimplifiedCartDetailDTO dto = convertCartDetailToDTO(cartDetail);

        RestResponse<SimplifiedCartDetailDTO> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Thêm sản phẩm vào giỏ hàng thành công!");
        response.setData(dto);

        return response;
    }

    // delete sản phẩm trong giỏ hàng
    @DeleteMapping("/carts/{cartId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId); // gọi service
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // delete all cart
    @DeleteMapping("/carts/{userId}/clears")
    public ResponseEntity<Void> clearAllCart(@PathVariable Long userId) {
        cartService.clearCartUserId(userId);
        return ResponseEntity.noContent().build();
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

    // delete one product in cart page
    @DeleteMapping("/carts/remove")
    public ResponseEntity<RestResponse<Void>> removeCartItem(
            @RequestParam Long userId,
            @RequestParam Long productId) {

        cartService.deleteItemFromCart(userId, productId);

        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Đã xóa sản phẩm khỏi giỏ hàng");
        response.setData(null);

        return ResponseEntity.ok(response);
    }

    public record UpdateCartQuantityRequest(Long userId, Long productId, Long quantity) {
    }

    @PutMapping("/carts/update")
    public RestResponse<Void> updateQuantity(@RequestBody UpdateCartQuantityRequest request) {
        cartService.updateQuantity(request.userId(), request.productId(), request.quantity());
        RestResponse<Void> res = new RestResponse<>();
        res.setStatusCode(200);
        res.setMessage("Cập nhật số lượng thành công");
        return res;
    }
}