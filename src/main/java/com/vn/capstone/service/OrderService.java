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
import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.User;
import com.vn.capstone.domain.response.order.OrderHistoryDTO;
import com.vn.capstone.domain.response.order.OrderItemDTO;
import com.vn.capstone.domain.response.order.OrderStatusHistoryDTO;
import com.vn.capstone.domain.response.order.OrderSummaryDTO;
import com.vn.capstone.domain.response.order.UpdateOrderRequest;
import com.vn.capstone.mapping.OrderMapper;
import com.vn.capstone.repository.CartDetailRepository;
import com.vn.capstone.repository.CartRepository;
import com.vn.capstone.repository.OrderDetailRepository;
import com.vn.capstone.repository.OrderRepository;
import com.vn.capstone.repository.OrderStatusHistoryRepository;
import com.vn.capstone.repository.ProductRepository;
import com.vn.capstone.repository.UserRepository;
import com.vn.capstone.util.constant.OrderStatus;
import com.vn.capstone.util.constant.PaymentMethod;
import com.vn.capstone.util.error.AccessDeniedException;
import com.vn.capstone.util.error.NotFoundException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository,
            CartRepository cartRepository, CartDetailRepository cartDetailRepository,
            OrderStatusHistoryRepository orderStatusHistoryRepository, UserRepository userRepository,
            OrderMapper orderMapper, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order placeOrder(Long userId, String receiverName,
            String address, String phone) {

        // Lấy Cart của user, kiểm tra rỗng
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null || cart.getCartDetails().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Tạo Order (chưa set totalPrice ngay)
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setReceiverName(receiverName);
        order.setReceiverAddress(address);
        order.setReceiverPhone(phone);
        order.setStatus(OrderStatus.PENDING);
        order = orderRepository.save(order); // lưu trước để có ID

        // Tính tổng tiền từ CartDetail
        double totalPrice = 0;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartDetail cd : cart.getCartDetails()) {
            // Lấy product với khóa ghi
            Product product = productRepository.findByIdForUpdate(cd.getProduct().getId());
            if (product == null) {
                throw new RuntimeException("Sản phẩm không tồn tại: " + cd.getProduct().getId());
            }

            int currentQuantity = Integer.parseInt(product.getQuantity());
            if (currentQuantity < cd.getQuantity()) {
                throw new RuntimeException("Hết hàng: " + product.getName());
            }

            // Trừ số lượng tồn
            product.setQuantity(String.valueOf(currentQuantity - cd.getQuantity()));
            productRepository.save(product);

            // Tính tổng tiền
            totalPrice += cd.getPrice() * cd.getQuantity();

            // Tạo OrderDetail
            OrderDetail od = new OrderDetail();
            od.setOrder(order);
            od.setProduct(product);
            od.setQuantity(cd.getQuantity());
            od.setPrice(cd.getPrice());
            od.setProductNameSnapshot(product.getName());
            od.setProductImageSnapshot(product.getImage());
            orderDetails.add(od);
        }

        // Set tổng tiền sau khi tính xong
        order.setTotalPrice(totalPrice);
        orderRepository.save(order); // update lại order đã có totalPrice

        // Lưu danh sách OrderDetail
        orderDetailRepository.saveAll(orderDetails);

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

        // Tính tổng số lượng
        long totalQuantity = orderDetailRepository.findByOrder(order).stream()
                .mapToLong(OrderDetail::getQuantity)
                .sum();
        dto.setTotalQuantity(totalQuantity);

        return dto;
    }

    // user delete order @PostMapping("/{id}/cancel")
    public void cancelOrder(Long orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // check user vs username
        if (!order.getUser().getEmail().equals(username)) {
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

    // take order of user of @GetMapping("/my-orders")
    public List<OrderSummaryDTO> getOrderSummariesForUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("Không tìm thấy người dùng");
        }

        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream()
                .map(this::toSummaryDTO) // sử dụng hàm mapping
                .collect(Collectors.toList());
    }

    /// in order of user
    public OrderSummaryDTO toSummaryDTO(Order order) {
        OrderSummaryDTO dto = new OrderSummaryDTO();
        dto.setId(order.getId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverAddress(order.getReceiverAddress());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setStatus(order.getStatus());
        dto.setUserId(order.getUser().getId());

        // Tổng số lượng sản phẩm
        long totalQuantity = orderDetailRepository.findByOrder(order).stream()
                .mapToLong(OrderDetail::getQuantity)
                .sum();
        dto.setTotalQuantity(totalQuantity);

        return dto;
    }

    // save order in order status history
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

    // người dùng khi click vào orderId có thể xem chi tiết sản phẩm
    public OrderHistoryDTO getOrderDetails(Long orderId, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!order.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Bạn không có quyền truy cập đơn hàng này");
        }

        return orderMapper.toOrderHistoryDTO(order);
    }

    // refresh quantity in data
    @Transactional
    public void processOrder(List<OrderItemDTO> orderItems) {
        for (OrderItemDTO item : orderItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            long currentQuantity = Long.parseLong(product.getQuantity());

            if (currentQuantity < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            long newQuantity = currentQuantity - item.getQuantity();
            product.setQuantity(String.valueOf(newQuantity));

            productRepository.save(product);
        }
    }

    // save value of vnpay
    @Transactional
    public void handleVNPAYSuccess(Long userId, String paymentRef, double totalAmount, String receiverName,
            String receiverAddress, String receiverPhone) {
        // Lấy Cart của user, kiểm tra rỗng
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null || cart.getCartDetails().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Tạo Order
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setReceiverName(cart.getUser().getName()); // Giả sử tên người nhận là tên của user
        order.setReceiverAddress(cart.getUser().getAddress()); // Giả sử địa chỉ người nhận là địa chỉ của user
        order.setStatus(OrderStatus.PENDING); // Trạng thái ban đầu là "Pending"
        order.setTotalPrice(totalAmount); // Sử dụng giá trị từ VNPAY hoặc tính toán lại
        order.setPaymentRef(paymentRef); // Lưu tham chiếu thanh toán từ VNPAY
        order.setReceiverName(receiverName);
        order.setReceiverAddress(receiverAddress);
        order.setReceiverPhone(receiverPhone);
        order = orderRepository.save(order); // lưu để có ID

        // Chuyển CartDetail thành OrderDetail
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartDetail cd : cart.getCartDetails()) {
            Product product = productRepository.findByIdForUpdate(cd.getProduct().getId());
            if (product == null) {
                throw new RuntimeException("Sản phẩm không tồn tại: " + cd.getProduct().getId());
            }

            int currentQuantity = Integer.parseInt(product.getQuantity());
            if (currentQuantity < cd.getQuantity()) {
                throw new RuntimeException("Hết hàng: " + product.getName());
            }

            // Trừ số lượng tồn
            product.setQuantity(String.valueOf(currentQuantity - cd.getQuantity()));
            productRepository.save(product); // lưu lại số lượng mới

            // Tạo OrderDetail
            OrderDetail od = new OrderDetail();
            od.setOrder(order);
            od.setProduct(product);
            od.setQuantity(cd.getQuantity());
            od.setPrice(cd.getPrice());
            od.setProductNameSnapshot(product.getName());
            od.setProductImageSnapshot(product.getImage());
            orderDetails.add(od);
        }

        orderDetailRepository.saveAll(orderDetails); // bulk insert

        // Clear Cart
        cartDetailRepository.deleteAllInBatch(cart.getCartDetails());
        cart.setSum(0); // Reset tổng tiền giỏ hàng
        cartRepository.save(cart);

        // Sau khi lưu đơn hàng, có thể gửi thông báo thành công hoặc làm gì đó khác
    }

    // cập nhập order bởi admin
    public void updateOrder(Long id, UpdateOrderRequest request, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        order.setReceiverName(request.getReceiverName());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setStatus(request.getStatus());
        order.setPaymentStatus(request.getPaymentStatus());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingMethod(request.getShippingMethod());
        order.setTrackingCode(request.getTrackingCode());
        order.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());
        order.setUpdatedAt(Instant.now());

        orderRepository.save(order);
    }

}