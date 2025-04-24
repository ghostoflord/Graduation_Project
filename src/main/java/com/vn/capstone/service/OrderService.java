package com.vn.capstone.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.capstone.domain.Cart;
import com.vn.capstone.domain.CartDetail;
import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.OrderDetail;
import com.vn.capstone.domain.response.order.OrderSummaryDTO;
import com.vn.capstone.repository.CartDetailRepository;
import com.vn.capstone.repository.CartRepository;
import com.vn.capstone.repository.OrderDetailRepository;
import com.vn.capstone.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository,
            CartRepository cartRepository, CartDetailRepository cartDetailRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
    }

    @Transactional
    public Order placeOrder(Long userId, String receiverName,
            String address, String phone) {

        // Lấy Cart của user, kiểm tra rỗng
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null || cart.getCartDetails().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Tạo Order
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setReceiverName(receiverName);
        order.setReceiverAddress(address);
        order.setReceiverPhone(phone);
        order.setStatus("PENDING");
        order.setTotalPrice(cart.getSum());
        order = orderRepository.save(order); // lưu để có ID

        // chuyển CartDetail thành OrderDetail
        // nếu có cart.getCartDetails() không cần query lại
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartDetail cd : cart.getCartDetails()) {
            OrderDetail od = new OrderDetail();
            od.setOrder(order);
            od.setProduct(cd.getProduct());
            od.setQuantity(cd.getQuantity());
            od.setPrice(cd.getPrice());
            orderDetails.add(od);
        }
        orderDetailRepository.saveAll(orderDetails); // bulk insert

        // Clear Cart
        cartDetailRepository.deleteAllInBatch(cart.getCartDetails());
        cart.setSum(0);
        cartRepository.save(cart);

        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<OrderSummaryDTO> getAllOrderSummaries() {
        return orderRepository.findAll() // trả về List<Order>
                .stream()
                .map(this::toDto) // map sang DTO
                .toList();
    }

    public OrderSummaryDTO toDto(Order order) {
        OrderSummaryDTO dto = new OrderSummaryDTO();
        dto.setId(order.getId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverAddress(order.getReceiverAddress());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setStatus(order.getStatus());
        dto.setUserId(order.getUser().getId());
        return dto;
    }
}