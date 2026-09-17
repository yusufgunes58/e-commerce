package com.example.ecommerce.category.service;

import com.example.ecommerce.category.entity.Category;
import com.example.ecommerce.category.repository.CategoryRepository;
import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.common.exception.ErrorCode;
import com.example.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductService productService;

    public List<Category> getCategoriesOrderAsc() {
        log.debug("Fetching active categories ordered by name");
        return categoryRepository.findAllByOrderByNameAsc();
    }

    public Category getById(Long id) {
        log.debug("Fetching category by id: {}", id);

        return categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Category not found. categoryId={}", id);
            return new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        });
    }

    @Transactional
    public Category create(String name) {
        String normalizedName = normalizeName(name);

        log.debug("Checking category name availability. name={}", normalizedName);

        if (categoryRepository.existsByName(normalizedName)) {
            log.warn("Category creation failed. Name already exists. name={}", normalizedName);
            throw new BusinessException(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS);
        }

        Category category = new Category(normalizedName);
        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully. categoryId={}, name={}", savedCategory.getId(), savedCategory.getName());

        return savedCategory;
    }

    @Transactional
    public void deleteCategory(Long categoryId, boolean confirmed) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        long productCount = productService.countByCategoryId(categoryId);

        if (productCount > 0 && !confirmed) {
            throw new BusinessException(
                    ErrorCode.CATEGORY_DELETE_CONFIRMATION_REQUIRED
            );
        }

        categoryRepository.delete(category);

        log.info(
                "Category deleted successfully. categoryId={}, productCount={}",
                categoryId,
                productCount
        );
    }

    @Transactional
    public Category updateName(Long categoryId, String name) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        String normalizedName = normalizeName(name);

        if (categoryRepository.existsByNameAndIdNot(
                normalizedName,
                categoryId
        )) {
            throw new BusinessException(
                    ErrorCode.CATEGORY_NAME_ALREADY_EXISTS
            );
        }

        category.setName(normalizedName);

        Category updatedCategory = categoryRepository.save(category);

        log.info(
                "Category name updated successfully. categoryId={}, name={}",
                categoryId,
                normalizedName
        );

        return updatedCategory;
    }



    private String normalizeName(String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }
}
