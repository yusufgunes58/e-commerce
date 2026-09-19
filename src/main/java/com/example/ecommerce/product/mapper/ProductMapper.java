package com.example.ecommerce.product.mapper;

import com.example.ecommerce.category.entity.Category;
import com.example.ecommerce.product.dto.response.ProductImageResponse;
import com.example.ecommerce.product.dto.response.customer.ProductDetailResponse;
import com.example.ecommerce.product.dto.response.customer.ProductVariantResponse;
import com.example.ecommerce.product.entity.Product;
import com.example.ecommerce.product.entity.ProductImage;
import com.example.ecommerce.product.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductVariantResponse toVariantResponse(ProductVariant variant);

    ProductImageResponse toImageResponse(ProductImage image);

    default String toCategoryName(Category category) {
        return category.getName();
    }

    @Mapping(target = "categories", source = "product.categories")
    @Mapping(target = "variants", source = "variants")
    @Mapping(target = "images", source = "images")
    ProductDetailResponse toDetailResponse(
            Product product,
            List<ProductVariant> variants,
            List<ProductImage> images
    );
}
