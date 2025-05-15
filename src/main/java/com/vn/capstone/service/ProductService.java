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
import com.vn.capstone.domain.response.ResProductDTO;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.domain.response.product.ProductUpdateRequest;
import com.vn.capstone.repository.CommentRepository;
import com.vn.capstone.repository.OrderDetailRepository;
import com.vn.capstone.repository.OrderRepository;
import com.vn.capstone.repository.ProductRepository;
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
import java.util.Base64;
import java.io.*;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CommentRepository commentRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;

    public ProductService(ProductRepository productRepository, CommentRepository commentRepository,
            OrderDetailRepository orderDetailRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.commentRepository = commentRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
    }

    public ResultPaginationDTO fetchAllProduct(Specification<Product> spec, Pageable pageable) {
        Page<Product> pageProduct = this.productRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageProduct.getTotalPages());
        mt.setTotal(pageProduct.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageProduct.getContent());

        // remove sensitive data
        List<ResProductDTO> listProduct = pageProduct.getContent()
                .stream().map(item -> this.convertToResProductDTO(item))
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

}
