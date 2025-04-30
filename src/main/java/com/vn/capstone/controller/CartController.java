package com.vn.capstone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.CartDetail;
import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.SimplifiedCartDetailDTO;
import com.vn.capstone.domain.response.cart.AddToCartRequest;
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
    @GetMapping("/users/{userId}")
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
    @PostMapping("/addproduct")
    public RestResponse<SimplifiedCartDetailDTO> addProductToCartJson(@RequestBody AddToCartRequest request) {
        Product product = new Product();
        product.setId(request.getProductId());

        CartDetail cartDetail = cartService.addCartDetail(
                request.getUserId(),
                product,
                request.getQuantity(),
                request.getPrice());

        SimplifiedCartDetailDTO dto = convertCartDetailToDTO(cartDetail);

        RestResponse<SimplifiedCartDetailDTO> response = new RestResponse<>();
        response.setStatusCode(200); // hoặc HttpStatus.OK.value()
        response.setMessage("Thêm sản phẩm vào giỏ hàng thành công!");
        response.setData(dto);
        System.out.println("Received request: " + request);
        return response;
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

}