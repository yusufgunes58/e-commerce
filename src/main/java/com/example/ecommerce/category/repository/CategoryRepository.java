package com.example.ecommerce.category.repository;

import com.example.ecommerce.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    List<Category> findAllByOrderByNameAsc();
    boolean existsByNameAndIdNot(String name, Long id);
}
