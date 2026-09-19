package com.example.ecommerce.product.service;

import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.common.exception.ErrorCode;
import com.example.ecommerce.product.dto.response.customer.ProductDetailResponse;
import com.example.ecommerce.product.dto.response.customer.ProductListResponse;
import com.example.ecommerce.product.entity.Product;
import com.example.ecommerce.product.entity.ProductImage;
import com.example.ecommerce.product.entity.ProductVariant;
import com.example.ecommerce.product.mapper.ProductMapper;
import com.example.ecommerce.product.repository.ProductImageRepository;
import com.example.ecommerce.product.repository.ProductRepository;

import com.example.ecommerce.product.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;

    // Category Count - using category service
    public long countByCategoryId(Long categoryId) {
        log.debug("Counting products by category. categoryId={}", categoryId);

        return productRepository.countByCategories_Id(categoryId);
    }

    // Public-Customer : Get All Active Products
    public Slice<ProductListResponse> getPublicProducts(
            Long categoryId,
            Pageable pageable
    ) {

        log.debug(
                "Fetching public products. categoryId={}, page={}, size={}",
                categoryId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Slice<ProductListResponse> products =
                productRepository.findPublicProducts(categoryId, pageable);

        log.debug(
                "Fetched {} public products from page {}",
                products.getNumberOfElements(),
                products.getNumber()
        );

        return products;
    }

    public ProductDetailResponse getPublicProductById(Long productId) {

        log.debug("Fetching public product detail. productId={}", productId);

        Product product = productRepository.findPublicProductById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        List<ProductVariant> variants =
                productVariantRepository
                        .findAllByProductIdAndActiveTrueOrderByIdAsc(productId);

        List<ProductImage> images =
                productImageRepository
                        .findAllByProductIdOrderBySortOrderAsc(productId);

        ProductDetailResponse response =
                productMapper.toDetailResponse(product, variants, images);

        log.debug(
                "Fetched public product detail. productId={}, variantCount={}, imageCount={}",
                productId,
                variants.size(),
                images.size()
        );

        return response;
    }

}
