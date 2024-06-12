package com.example.back_end.repository;

import com.example.back_end.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findByNameContainingAndPublished(String name, Boolean published, Pageable pageable);

    Page<Category> findByNameContaining(String name, Pageable pageable);

}