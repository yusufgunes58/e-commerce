package com.example.ecommerce.product.service;

import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.common.exception.ErrorCode;
import com.example.ecommerce.product.dto.response.FindVariantForCart;
import com.example.ecommerce.product.dto.response.integrationCart.CartProductVariant;
import com.example.ecommerce.product.entity.ProductVariant;
import com.example.ecommerce.product.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductVariantService {

    private  final ProductVariantRepository productVariantRepository;

    public FindVariantForCart getVariantInfo(Long variantId) {

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND)
                );

        return new FindVariantForCart(
                variant.getId(),
                Boolean.TRUE.equals(variant.getActive()),
                variant.getStockQuantity()
        );

    }

        public Map<Long, CartProductVariant> findCartProductVariants(
                List<Long> variantIds
        ) {
            if (variantIds.isEmpty()) {
                return Map.of();
            }

            return productVariantRepository
                    .findCartProductVariants(variantIds)
                    .stream()
                    .collect(Collectors.toMap(
                            CartProductVariant::variantId,
                            Function.identity()
                    ));
        }



}
