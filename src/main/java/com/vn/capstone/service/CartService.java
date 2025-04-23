package com.vn.capstone.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.capstone.domain.Cart;
import com.vn.capstone.domain.CartDetail;
import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.User;
import com.vn.capstone.repository.CartDetailRepository;
import com.vn.capstone.repository.CartRepository;
import com.vn.capstone.repository.ProductRepository;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartDetailRepository cartDetailRepository,
            ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.productRepository = productRepository;
    }

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public CartDetail addCartDetail(Long userId, Product product, long quantity, double price) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(new User(userId));
            cart.setSum(0);
            cart = cartRepository.save(cart);
        }

        CartDetail detail = new CartDetail();
        detail.setCart(cart);
        detail.setProduct(product);
        detail.setQuantity(quantity);
        detail.setPrice(price);
        CartDetail savedDetail = cartDetailRepository.save(detail);

        cart.setSum(cart.getSum() + (int) (price * quantity));
        cartRepository.save(cart);
        recalcSum(cart);
        return savedDetail;
    }

    private void recalcSum(Cart cart) {
        long total = cartDetailRepository.sumQuantityByCart(cart.getId());
        cart.setSum(total);
        cartRepository.save(cart);
    }

    public List<CartDetail> getAllCartDetails() {
        return cartDetailRepository.findAll();
    }

    public List<CartDetail> getCartDetailsByCartId(Long cartId) {
        return cartDetailRepository.findByCartId(cartId);
    }

    public void clearCart(Cart cart) {
        List<CartDetail> details = cartDetailRepository.findByCartId(cart.getId());
        cartDetailRepository.deleteAll(details);
        cart.setSum(0);
        cartRepository.save(cart);
    }

}