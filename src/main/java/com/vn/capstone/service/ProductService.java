package com.vn.capstone.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.ProductDetail;
import com.vn.capstone.domain.response.CreateProductDTO;
import com.vn.capstone.domain.response.ResProductDTO;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.domain.response.compare.CompareProductDTO;
import com.vn.capstone.domain.response.product.ProductDTO;
import com.vn.capstone.domain.response.product.ProductDetailDTO;
import com.vn.capstone.domain.response.product.ProductUpdateRequest;
import com.vn.capstone.domain.specification.ProductSpecifications;
import com.vn.capstone.repository.CommentRepository;
import com.vn.capstone.repository.FlashSaleItemRepository;
import com.vn.capstone.repository.LikeRepository;
import com.vn.capstone.repository.OrderDetailRepository;
import com.vn.capstone.repository.OrderRepository;
import com.vn.capstone.repository.ProductRepository;
import com.vn.capstone.repository.ReviewRepository;
import com.vn.capstone.util.SlugUtils;
import com.vn.capstone.util.constant.PaymentStatus;

import jakarta.transaction.Transactional;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Base64;
import java.io.*;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CommentRepository commentRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final LikeRepository likeRepository;
    private final FlashSaleItemRepository flashSaleItemRepository;

    public ProductService(ProductRepository productRepository, CommentRepository commentRepository,
            OrderDetailRepository orderDetailRepository, OrderRepository orderRepository,
            ReviewRepository reviewRepository, LikeRepository likeRepository,
            FlashSaleItemRepository flashSaleItemRepository) {
        this.productRepository = productRepository;
        this.commentRepository = commentRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
        this.likeRepository = likeRepository;
        this.flashSaleItemRepository = flashSaleItemRepository;
    }

    public ResultPaginationDTO fetchAllProduct(Specification<Product> spec, Pageable pageable) {
        // Lấy danh sách ID của sản phẩm đang trong Flash Sale
        List<Long> flashSaleProductIds = flashSaleItemRepository.findAll()
                .stream()
                .map(item -> item.getProduct().getId())
                .toList();

        // Kết hợp specification hiện có với bộ lọc không flash sale
        Specification<Product> finalSpec = spec.and(
                ProductSpecifications.notInFlashSale(flashSaleProductIds));

        // Lấy danh sách sản phẩm đã lọc
        Page<Product> pageProduct = this.productRepository.findAll(finalSpec, pageable);

        // Build response
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageProduct.getTotalPages());
        mt.setTotal(pageProduct.getTotalElements());
        rs.setMeta(mt);

        List<ResProductDTO> listProduct = pageProduct.getContent()
                .stream()
                .map(this::convertToResProductDTO)
                .collect(Collectors.toList());

        rs.setResult(listProduct);
        return rs;
    }

    public Product fetchProductById(long id) {
        Optional<Product> ProductOptional = this.productRepository.findById(id);
        if (ProductOptional.isPresent()) {
            return ProductOptional.get();
        }
        return null;
    }

    // Thêm fetchProductBySlug
    public Product fetchProductBySlug(String slug) {
        Optional<Product> ProductOptional = this.productRepository.findBySlug(slug);
        if (ProductOptional.isPresent()) {
            return ProductOptional.get();
        }
        return null;
    }

    public void updateSlugsForExistingProducts() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            if (product.getSlug() == null || product.getSlug().isEmpty()) {
                String generatedSlug = SlugUtils.toSlug(product.getName());
                product.setSlug(generatedSlug);
            }
        }
        productRepository.saveAll(products);
    }

    public Product handleUpdateProduct(Product reqProduct) {
        Product currentProduct = this.fetchProductById(reqProduct.getId());
        if (currentProduct != null) {
            currentProduct.setName(reqProduct.getName());
            currentProduct.setDetailDescription(reqProduct.getDetailDescription());
            currentProduct.setImage(reqProduct.getImage());
            currentProduct.setFactory(reqProduct.getFactory());
            currentProduct.setPrice(reqProduct.getPrice());
            currentProduct.setQuantity(reqProduct.getQuantity());
            currentProduct.setSold(reqProduct.getSold());
            currentProduct.setShortDescription(reqProduct.getShortDescription());
            currentProduct.setGuarantee(reqProduct.getGuarantee());
            currentProduct.setProductCode(reqProduct.getProductCode());
            currentProduct.setBestsell(reqProduct.getBestsell());
            currentProduct.setSell(reqProduct.getSell());
            // update
            currentProduct = this.productRepository.save(currentProduct);
        }
        return currentProduct;
    }

    public Product handleCreateProduct(Product product) {
        if (product.getSlug() == null || product.getSlug().isEmpty()) {
            product.setSlug(SlugUtils.toSlug(product.getName()));
        }

        return this.productRepository.save(product);
    }

    @Transactional
    public void handleDeleteProduct(long id) {
        this.commentRepository.deleteByProductId(id);
        // Xóa order_detail liên quan
        orderDetailRepository.deleteByProductId(id);
        this.productRepository.deleteById(id);
    }

    public ResProductDTO convertToResProductDTO(Product product) {
        ResProductDTO res = new ResProductDTO();
        res.setId(product.getId());
        res.setDetailDescription(product.getDetailDescription());
        res.setName(product.getName());
        res.setProductCode(product.getProductCode());
        res.setImage(product.getImage());
        res.setGuarantee(product.getGuarantee());
        res.setFactory(product.getFactory());
        res.setPrice(product.getPrice());
        res.setQuantity(product.getQuantity());
        res.setSold(product.getSold());
        res.setShortDescription(product.getShortDescription());
        res.setBestsell(product.getBestsell());
        res.setSell(product.getSell());

        Double avgRating = reviewRepository.getAverageRatingByProductId(product.getId());
        Long totalReview = likeRepository.countByProductId(product.getId());

        res.setAverageRating(avgRating != null ? avgRating : 0.0);
        res.setTotalReviews(totalReview != null ? totalReview : 0L);

        return res;
    }

    // test thuss
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // vnpay
    public void updatePaymentStatus(String paymentRef, String paymentStatus) {
        Optional<Order> orderOptional = this.orderRepository.findByPaymentRef(paymentRef);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            try {
                PaymentStatus statusEnum = PaymentStatus.valueOf(paymentStatus.toUpperCase());
                order.setPaymentStatus(statusEnum);
                this.orderRepository.save(order);
            } catch (IllegalArgumentException ex) {
                // Trường hợp String không hợp lệ, log hoặc xử lý lỗi
                System.err.println("Invalid payment status value: " + paymentStatus);
            }
        }
    }

    // lấy thông tin rõ hơn của sản phẩm
    public ProductDTO fetchProductDTOById(long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return null;
        }

        Product product = productOptional.get();
        ProductDTO dto = new ProductDTO();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setProductCode(product.getProductCode());
        dto.setDetailDescription(product.getDetailDescription());
        dto.setGuarantee(product.getGuarantee());
        dto.setImage(product.getImage());
        dto.setFactory(product.getFactory());
        dto.setPrice(product.getPrice());
        dto.setSold(product.getSold());
        dto.setQuantity(product.getQuantity());
        dto.setShortDescription(product.getShortDescription());
        dto.setSlug(product.getSlug());
        dto.setSell(product.getSell());
        dto.setBestsell(product.getBestsell());
        dto.setCategory(product.getCategory());

        // Map ProductDetail nếu có
        if (product.getProductDetail() != null) {
            ProductDetail detail = product.getProductDetail();
            ProductDetailDTO detailDTO = new ProductDetailDTO();
            detailDTO.setCpu(detail.getCpu());
            detailDTO.setRam(detail.getRam());
            detailDTO.setStorage(detail.getStorage());
            detailDTO.setGpu(detail.getGpu());
            detailDTO.setScreen(detail.getScreen());
            detailDTO.setBattery(detail.getBattery());
            detailDTO.setWeight(detail.getWeight());
            detailDTO.setMaterial(detail.getMaterial());
            detailDTO.setOs(detail.getOs());
            detailDTO.setSpecialFeatures(detail.getSpecialFeatures());
            detailDTO.setPorts(detail.getPorts());

            dto.setDetail(detailDTO);
        }

        return dto;
    }

    public List<CompareProductDTO> getProductsForComparison(List<Long> ids) {
        List<Product> products = productRepository.findAllByIdIn(ids);
        List<CompareProductDTO> result = new ArrayList<>();

        for (Product product : products) {
            ProductDetail detail = product.getProductDetail();
            if (detail == null)
                continue;

            CompareProductDTO dto = new CompareProductDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setImage(product.getImage());

            dto.setCpu(detail.getCpu());
            dto.setRam(detail.getRam());
            dto.setStorage(detail.getStorage());
            dto.setGpu(detail.getGpu());
            dto.setScreen(detail.getScreen());
            dto.setBattery(detail.getBattery());
            dto.setWeight(detail.getWeight());
            dto.setMaterial(detail.getMaterial());
            dto.setOs(detail.getOs());
            dto.setSpecialFeatures(detail.getSpecialFeatures());
            dto.setPorts(detail.getPorts());

            result.add(dto);
        }

        return result;
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    public String generateSKU(CreateProductDTO dto) {
        ProductDetailDTO detail = dto.getDetail();
        if (detail == null) {
            throw new IllegalArgumentException("Chi tiết sản phẩm không được null để tạo SKU");
        }

        String brand = dto.getFactory().toUpperCase(); // factory tương ứng brand
        String model = dto.getName().toUpperCase();
        String cpu = detail.getCpu().toUpperCase().replaceAll("\\s+", "");
        String ram = detail.getRam().toUpperCase();
        String storage = detail.getStorage().toUpperCase();
        return String.format("%s-%s-%s-%s-%s", brand, model, cpu, ram, storage);
    }

}
