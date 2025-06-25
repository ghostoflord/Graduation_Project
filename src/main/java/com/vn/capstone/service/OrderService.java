package com.vn.capstone.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.capstone.domain.Cart;
import com.vn.capstone.domain.CartDetail;
import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.OrderDetail;
import com.vn.capstone.domain.OrderStatusHistory;
import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.User;
import com.vn.capstone.domain.Voucher;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.domain.response.order.OrderDiscountResult;
import com.vn.capstone.domain.response.order.OrderHistoryDTO;
import com.vn.capstone.domain.response.order.OrderItemDTO;
import com.vn.capstone.domain.response.order.OrderShipperDTO;
import com.vn.capstone.domain.response.order.OrderStatusHistoryDTO;
import com.vn.capstone.domain.response.order.OrderSummaryDTO;
import com.vn.capstone.domain.response.order.UpdateOrderRequest;
import com.vn.capstone.domain.response.shipper.ShipperStatsResponse;
import com.vn.capstone.mapping.OrderMapper;
import com.vn.capstone.repository.CartDetailRepository;
import com.vn.capstone.repository.CartRepository;
import com.vn.capstone.repository.OrderDetailRepository;
import com.vn.capstone.repository.OrderRepository;
import com.vn.capstone.repository.OrderStatusHistoryRepository;
import com.vn.capstone.repository.ProductRepository;
import com.vn.capstone.repository.UserRepository;
import com.vn.capstone.repository.VoucherRepository;
import com.vn.capstone.util.constant.OrderStatus;
import com.vn.capstone.util.constant.PaymentMethod;
import com.vn.capstone.util.error.AccessDeniedException;
import com.vn.capstone.util.error.NotFoundException;

import jakarta.annotation.Nullable;

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
    private final VoucherRepository voucherRepository;
    private final FlashSaleService flashSaleService;
    @Autowired
    private VoucherService voucherService;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository,
            CartRepository cartRepository, CartDetailRepository cartDetailRepository,
            OrderStatusHistoryRepository orderStatusHistoryRepository, UserRepository userRepository,
            OrderMapper orderMapper, ProductRepository productRepository, VoucherRepository voucherRepository,
            FlashSaleService flashSaleService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
        this.productRepository = productRepository;
        this.voucherRepository = voucherRepository;
        this.flashSaleService = flashSaleService;
    }

    public Optional<OrderSummaryDTO> getOrderSummaryById(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderOpt.get();
        OrderSummaryDTO dto = new OrderSummaryDTO();

        dto.setId(order.getId());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverAddress(order.getReceiverAddress());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setUserId(order.getUser().getId()); // giả sử Order có getUser()

        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setShippingMethod(order.getShippingMethod());
        dto.setTrackingCode(order.getTrackingCode());
        dto.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        // Tính tổng quantity từ orderDetails
        long totalQuantity = 0;
        if (order.getOrderDetails() != null) {
            for (OrderDetail detail : order.getOrderDetails()) {
                totalQuantity += detail.getQuantity();
            }
        }
        dto.setTotalQuantity(totalQuantity);

        return Optional.of(dto);
    }

    @Transactional
    public Order placeOrder(Long userId, String receiverName,
            String address, String phone, String voucherCode, @Nullable Long flashSaleItemId) {

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
        order = orderRepository.save(order); // Lưu trước để có ID

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

            long quantityLong = cd.getQuantity();
            if (quantityLong > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Số lượng quá lớn, không thể xử lý");
            }
            if (flashSaleItemId != null) {
                flashSaleService.reduceFlashSaleItemQuantity(flashSaleItemId, (int) quantityLong);
            }
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

        double finalAmount = totalPrice;
        long discountAmount = 0;

        // Áp dụng voucher nếu có
        if (voucherCode != null && !voucherCode.isBlank()) {
            // Gọi applyVoucher để vừa kiểm tra, vừa lưu lịch sử (user_voucher)
            OrderDiscountResult discountResult = voucherService.applyVoucher(
                    voucherCode,
                    userId,
                    (int) totalPrice,
                    true // ✅ Đảm bảo là lưu
            );

            discountAmount = discountResult.getDiscountAmount();
            finalAmount = discountResult.getFinalAmount();

            // Lấy voucher từ repository và set vào order
            Voucher voucher = voucherRepository.findByCode(voucherCode);
            order.setVoucher(voucher);
        }

        // Lưu order với giá cuối cùng
        order.setTotalPrice(finalAmount);
        order.setDiscountedPrice(finalAmount); // bạn có thể tách nếu cần
        orderRepository.save(order);

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

        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
        } else {
            dto.setUserId(-1); // hoặc bỏ dòng này nếu không cần userId
        }

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

    @Transactional
    public void deleteOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Xóa toàn bộ OrderDetail trước
        List<OrderDetail> details = orderDetailRepository.findByOrder(order);
        orderDetailRepository.deleteAll(details); // xoá hết chi tiết

        orderStatusHistoryRepository.deleteAllByOrder(order);
        orderRepository.delete(order);
    }

    // shipper
    public ResultPaginationDTO fetchOrdersForShipper(String username, Specification<Order> spec, Pageable pageable) {
        Page<Order> pageOrder = orderRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageOrder.getTotalPages());
        meta.setTotal(pageOrder.getTotalElements());

        result.setMeta(meta);

        List<OrderShipperDTO> dtoList = pageOrder.getContent().stream()
                .map(this::convertToOrderShipperDTO)
                .collect(Collectors.toList());

        result.setResult(dtoList);
        return result;
    }

    public OrderShipperDTO convertToOrderShipperDTO(Order order) {
        return new OrderShipperDTO(
                order.getId(),
                order.getTotalPrice(),
                order.getReceiverName(),
                order.getReceiverAddress(),
                order.getReceiverPhone(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getPaymentMethod(),
                order.getPaymentRef(),
                order.getShippingMethod(),
                order.getTrackingCode(),
                order.getEstimatedDeliveryTime(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getDeliveredAt(),
                order.getCancelReason(),
                order.getUser() != null ? order.getUser().getName() : null,
                order.getUser() != null ? order.getUser().getEmail() : null);
    }

    public void acceptOrder(Long orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order already accepted or processed");
        }

        User shipper = userRepository.findByEmail(username);

        order.setShipper(shipper);
        order.setStatus(OrderStatus.SHIPPING); // chuyển sang đang giao

        orderRepository.save(order);
    }

    public void completeOrder(Long orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Chỉ đơn hàng đã giao mới có thể xác nhận nhận hàng");
        }

        order.setStatus(OrderStatus.COMPLETED); // Trạng thái cuối cùng
        order.setEstimatedDeliveryTime(Instant.now());

        orderRepository.save(order);
    }

    public void markAsDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (order.getStatus() != OrderStatus.SHIPPING) {
            throw new IllegalStateException("Chỉ đơn hàng đang giao mới được đánh dấu là đã giao");
        }

        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(Instant.now());

        orderRepository.save(order);
    }

    public List<OrderShipperDTO> getDeliveredOrdersForShipper(String username) {
        User shipper = userRepository.findByEmail(username);

        List<Order> deliveredOrders = (List<Order>) orderRepository.findByShipperAndStatus(shipper,
                OrderStatus.DELIVERED);
        List<OrderShipperDTO> dtoList = new ArrayList<>();

        for (Order order : deliveredOrders) {
            OrderShipperDTO dto = OrderShipperDTO.builder()
                    .id(order.getId())
                    .totalPrice(order.getTotalPrice())
                    .receiverName(order.getReceiverName())
                    .receiverAddress(order.getReceiverAddress())
                    .receiverPhone(order.getReceiverPhone())
                    .status(order.getStatus())
                    .paymentStatus(order.getPaymentStatus())
                    .paymentMethod(order.getPaymentMethod())
                    .paymentRef(order.getPaymentRef())
                    .shippingMethod(order.getShippingMethod())
                    .trackingCode(order.getTrackingCode())
                    .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                    .createdAt(order.getCreatedAt())
                    .updatedAt(order.getUpdatedAt())
                    .deliveredAt(order.getDeliveredAt())
                    .cancelReason(order.getCancelReason())
                    .customerName(order.getUser() != null ? order.getUser().getName() : null)
                    .customerEmail(order.getUser() != null ? order.getUser().getEmail() : null)
                    .build();

            dtoList.add(dto);
        }

        return dtoList;
    }

    //
    // public ShipperStatsResponse getShipperStats(Long shipperId, Instant from,
    // Instant to) {
    // long delivered = orderRepository.countByStatusAndShipperAndDateRange(
    // shipperId, OrderStatus.SHIPPING, from, to);

    // long failed = orderRepository.countByStatusAndShipperAndDateRange(
    // shipperId, OrderStatus.FAILED, from, to);

    // long inProgress = orderRepository.countByStatusAndShipperAndDateRange(
    // shipperId, OrderStatus.DELIVERED, from, to);

    // long cod = orderRepository.sumCODByShipperAndDateRange(shipperId, from, to);

    // return new ShipperStatsResponse(delivered, failed, inProgress, cod);
    // }

    public ShipperStatsResponse getShipperStats(Long shipperId) {
        // Đơn giao thành công (DELIVERED)
        long delivered = orderRepository.countByStatusAndShipper(shipperId, OrderStatus.DELIVERED);

        // Đơn đang giao (SHIPPING)
        long inProgress = orderRepository.countByStatusAndShipper(shipperId, OrderStatus.SHIPPING);

        // Đơn thất bại (CANCELED + RETURNED)
        long failed = orderRepository.countByStatusesAndShipper(
                shipperId, List.of(OrderStatus.CANCELED, OrderStatus.RETURNED));

        // Tổng COD = tổng totalPrice các đơn DELIVERED
        long codAmount = orderRepository.sumCODByShipper(shipperId);

        ShipperStatsResponse response = new ShipperStatsResponse();
        response.setDelivered(delivered);
        response.setFailed(failed);
        response.setInProgress(inProgress);
        response.setCodAmount(codAmount);

        return response;
    }

}