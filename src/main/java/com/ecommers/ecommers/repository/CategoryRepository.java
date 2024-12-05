package com.ecommers.ecommers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommers.ecommers.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByName(String name);
}
