package com.example.ecommerce.product.service;

import com.example.ecommerce.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class ProductService {

    private final ProductRepository productRepository;

    // Category Count
    public long countByCategoryId(Long categoryId) {
        log.debug("Counting products by category. categoryId={}", categoryId);

        return productRepository.countByCategories_Id(categoryId);
    }

}
