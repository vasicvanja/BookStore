package com.bookshop.service;

import com.bookshop.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    List<Category> findAllByName(String name);

    Category findById(Long id);

    Category save(Category category);

    Category update(Long id, String name, String description);

    void deleteById(Long id);
}
