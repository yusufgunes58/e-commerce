package com.example.ecommerce.category;

import com.example.ecommerce.category.dto.response.CategoryDeleteInfoResponse;
import com.example.ecommerce.category.entity.Category;
import com.example.ecommerce.category.repository.CategoryRepository;
import com.example.ecommerce.category.service.CategoryService;
import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.common.exception.ErrorCode;
import com.example.ecommerce.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Should return categories by name")
    void shouldReturnCategoriesByName() {
        Category laptop = new Category("Laptop");
        Category phone = new Category("Phone");

        List<Category> categories = List.of(laptop, phone);

        when(categoryRepository.findAllByOrderByNameAsc())
                .thenReturn(categories);

        List<Category> result = categoryService.getCategoriesOrderAsc();

        assertEquals(categories, result);

        verify(categoryRepository).findAllByOrderByNameAsc();
    }

    @Test
    @DisplayName("Should return category by ID")
    void shouldReturnCategoryById() {
        Long categoryId = 1L;
        Category category = new Category("Laptop");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        Category result = categoryService.getById(categoryId);

        assertEquals(category, result);

        verify(categoryRepository).findById(categoryId);
    }


    @Test
    @DisplayName("Should fail when category is missing")
    void shouldFailWhenCategoryIsMissing() {
        Long categoryId = 99L;

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> categoryService.getById(categoryId)
        );

        assertEquals(
                ErrorCode.CATEGORY_NOT_FOUND,
                exception.getErrorCode()
        );
    }

    @Test
    @DisplayName("Should create category by trimming spaces and converting to lowercase")
    void shouldCreateCategory_WhenNameHasUnnecessarySpaces() {
        // Arrange
        String inputName = "  Electronics  ";
        String expectedName = "electronics";
        Category savedCategory = new Category(expectedName);

        when(categoryRepository.existsByName(expectedName))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(savedCategory);

        // Act
        Category result = categoryService.create(inputName);

        // Assert
        assertNotNull(result);
        assertEquals(expectedName, result.getName());

        verify(categoryRepository).existsByName(expectedName);

        // Repository'ye giden nesnenin trim'lenmiş doğru ismi içerdiği doğrulanıyor
        verify(categoryRepository).save(argThat(
                category -> expectedName.equals(category.getName())
        ));
    }


    @Test
    @DisplayName("Should normalize name with trim and lowercase on create")
    void shouldNormalizeNameOnCreate() {
        String name = "  Electronics  ";
        Category savedCategory = new Category("electronics");

        when(categoryRepository.existsByName("electronics"))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(savedCategory);

        Category result = categoryService.create(name);

        assertEquals("electronics", result.getName());


        verify(categoryRepository).existsByName("electronics");
        verify(categoryRepository).save(argThat(
                category -> category.getName().equals("electronics")
        ));
    }

    @Test
    @DisplayName("Should fail for duplicate name")
    void shouldFailForDuplicateName() {
        String name = "Electronics";

        when(categoryRepository.existsByName("electronics"))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> categoryService.create(name)
        );

        assertEquals(
                ErrorCode.CATEGORY_NAME_ALREADY_EXISTS,
                exception.getErrorCode()
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }


    @Test
    @DisplayName("Should fail for duplicate name even with surrounding spaces")
    void shouldFailForDuplicateNameWithSpaces() {
        String name = "  Electronics  ";

        when(categoryRepository.existsByName("electronics"))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> categoryService.create(name)
        );

        assertEquals(
                ErrorCode.CATEGORY_NAME_ALREADY_EXISTS,
                exception.getErrorCode()
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    @DisplayName("Should update category name")
    void shouldUpdateCategoryName() {
        Long categoryId = 1L;
        String newName = "Electronics";

        Category category = new Category("Technology");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(categoryRepository.existsByNameAndIdNot(
                "electronics",
                categoryId
        )).thenReturn(false);

        when(categoryRepository.save(category))
                .thenReturn(category);

        Category result = categoryService.updateName(
                categoryId,
                newName
        );

        assertEquals("electronics", result.getName());

        verify(categoryRepository)
                .existsByNameAndIdNot("electronics", categoryId);

        verify(categoryRepository).save(category);
    }


    @Test
    @DisplayName("Should normalize name with trim and lowercase on update")
    void shouldNormalizeNameOnUpdate() {
        Long categoryId = 1L;
        String newName = "  Electronics  ";

        Category category = new Category("Technology");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(categoryRepository.existsByNameAndIdNot(
                "electronics",
                categoryId
        )).thenReturn(false);

        when(categoryRepository.save(category))
                .thenReturn(category);

        Category result = categoryService.updateName(
                categoryId,
                newName
        );

        assertEquals("electronics", result.getName());

        // trim + lowercase sonucu "electronics" ile duplicate kontrolü yapılıyor
        verify(categoryRepository)
                .existsByNameAndIdNot("electronics", categoryId);
    }

    @Test
    @DisplayName("Should fail for duplicate update name")
    void shouldFailForDuplicateUpdateName() {
        Long categoryId = 1L;
        String newName = "Electronics";

        Category category = new Category("Technology");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(categoryRepository.existsByNameAndIdNot(
                "electronics",
                categoryId
        )).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> categoryService.updateName(
                        categoryId,
                        newName
                )
        );

        assertEquals(
                ErrorCode.CATEGORY_NAME_ALREADY_EXISTS,
                exception.getErrorCode()
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }


    @Test
    @DisplayName("Should fail when updating missing category")
    void shouldFailWhenUpdatingMissingCategory() {
        Long categoryId = 99L;

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> categoryService.updateName(
                        categoryId,
                        "Electronics"
                )
        );

        assertEquals(
                ErrorCode.CATEGORY_NOT_FOUND,
                exception.getErrorCode()
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }


    @Test
    @DisplayName("Should delete empty category")
    void shouldDeleteEmptyCategory() {
        Long categoryId = 1L;
        Category category = new Category("Electronics");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productService.countByCategoryId(categoryId))
                .thenReturn(0L);

        categoryService.deleteCategory(categoryId, false);

        verify(categoryRepository).delete(category);
    }



    @Test
    @DisplayName("Should require delete confirmation")
    void shouldRequireDeleteConfirmation() {
        Long categoryId = 1L;
        Category category = new Category("Electronics");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productService.countByCategoryId(categoryId))
                .thenReturn(15L);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> categoryService.deleteCategory(
                        categoryId,
                        false
                )
        );

        assertEquals(
                ErrorCode.CATEGORY_DELETE_CONFIRMATION_REQUIRED,
                exception.getErrorCode()
        );

        verify(categoryRepository, never())
                .delete(any(Category.class));
    }



    @Test
    @DisplayName("Should delete confirmed category")
    void shouldDeleteConfirmedCategory() {
        Long categoryId = 1L;
        Category category = new Category("Electronics");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productService.countByCategoryId(categoryId))
                .thenReturn(15L);

        categoryService.deleteCategory(categoryId, true);

        verify(categoryRepository).delete(category);
    }



    @Test
    @DisplayName("Should fail when deleting missing category")
    void shouldFailWhenDeletingMissingCategory() {
        Long categoryId = 99L;

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> categoryService.deleteCategory(
                        categoryId,
                        false
                )
        );

        assertEquals(
                ErrorCode.CATEGORY_NOT_FOUND,
                exception.getErrorCode()
        );

        verify(productService, never())
                .countByCategoryId(anyLong());

        verify(categoryRepository, never())
                .delete(any(Category.class));
    }



    @Test
    @DisplayName("Should return category delete info")
    void shouldReturnCategoryDeleteInfo() {
        Long categoryId = 1L;
        Category category = new Category("Electronics");
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productService.countByCategoryId(categoryId))
                .thenReturn(15L);

        CategoryDeleteInfoResponse result =
                categoryService.getDeleteInfo(categoryId);

        assertEquals(categoryId, result.categoryId());
        assertEquals("Electronics", result.categoryName());
        assertEquals(15L, result.productCount());

        verify(productService).countByCategoryId(categoryId);
    }

    @Test
    @DisplayName("Should fail when delete info category is missing")
    void shouldFailWhenDeleteInfoCategoryIsMissing() {
        Long categoryId = 99L;

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> categoryService.getDeleteInfo(categoryId)
        );

        assertEquals(
                ErrorCode.CATEGORY_NOT_FOUND,
                exception.getErrorCode()
        );

        verify(productService, never())
                .countByCategoryId(anyLong());
    }


    @Test
    @DisplayName("Should return zero products for empty category")
    void shouldReturnZeroProductsForEmptyCategory() {
        Long categoryId = 1L;
        Category category = new Category("Electronics");
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productService.countByCategoryId(categoryId))
                .thenReturn(0L);

        CategoryDeleteInfoResponse result =
                categoryService.getDeleteInfo(categoryId);

        assertEquals(categoryId, result.categoryId());
        assertEquals("Electronics", result.categoryName());
        assertEquals(0L, result.productCount());

        verify(productService).countByCategoryId(categoryId);
    }
}