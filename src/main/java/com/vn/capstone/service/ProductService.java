package com.vn.capstone.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.response.ResProductDTO;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
            // update
            currentProduct = this.productRepository.save(currentProduct);
        }
        return currentProduct;
    }

    public Product handleCreateProduct(Product Product) {
        return this.productRepository.save(Product);
    }

    public void handleDeleteProduct(long id) {
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
        return res;
    }
}
