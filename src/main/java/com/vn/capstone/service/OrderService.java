package com.vn.capstone.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.capstone.domain.Cart;
import com.vn.capstone.domain.CartDetail;
import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.OrderDetail;
import com.vn.capstone.domain.OrderStatusHistory;
import com.vn.capstone.domain.response.order.OrderStatusHistoryDTO;
import com.vn.capstone.domain.response.order.OrderSummaryDTO;
import com.vn.capstone.repository.CartDetailRepository;
import com.vn.capstone.repository.CartRepository;
import com.vn.capstone.repository.OrderDetailRepository;
import com.vn.capstone.repository.OrderRepository;
import com.vn.capstone.repository.OrderStatusHistoryRepository;
import com.vn.capstone.util.constant.OrderStatus;
import com.vn.capstone.util.error.AccessDeniedException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository,
            CartRepository cartRepository, CartDetailRepository cartDetailRepository,
            OrderStatusHistoryRepository orderStatusHistoryRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
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
        order.setStatus(OrderStatus.PENDING);
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

    public void cancelOrder(Long orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!order.getUser().getName().equals(username)) {
            throw new AccessDeniedException("Không được phép hủy đơn này");
        }

        if (!(order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED)) {
            throw new IllegalStateException("Không thể hủy đơn đã vận chuyển hoặc giao hàng");
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELED);
        order.setUpdatedAt(Instant.now());
        order.setCancelReason("Người dùng yêu cầu hủy");

        orderRepository.save(order);

        // Ghi lịch sử sau khi lưu
        saveOrderStatusHistory(order, oldStatus, OrderStatus.CANCELED);
    }

    public List<OrderStatusHistoryDTO> getOrderStatusHistory(Long orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!order.getUser().getName().equals(username)) {
            throw new AccessDeniedException("Không được phép xem đơn hàng này");
        }

        return orderStatusHistoryRepository.findByOrderIdOrderByChangedAtDesc(orderId)
                .stream()
                .map(history -> new OrderStatusHistoryDTO(
                        history.getOldStatus(),
                        history.getNewStatus(),
                        history.getChangedAt()))
                .collect(Collectors.toList());
    }

    private void saveOrderStatusHistory(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedAt(Instant.now());
        orderStatusHistoryRepository.save(history);
    }

    public void requestReturn(Long orderId, String reason, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!order.getUser().getName().equals(username)) {
            throw new AccessDeniedException("Không được phép");
        }

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Chỉ có thể trả đơn đã giao");
        }

        order.setStatus(OrderStatus.RETURNED);
        order.setUpdatedAt(Instant.now());
        order.setCancelReason(reason); // hoặc tạo field riêng `returnReason`

        orderRepository.save(order);

        saveOrderStatusHistory(order, order.getStatus(), OrderStatus.RETURNED);
    }

}