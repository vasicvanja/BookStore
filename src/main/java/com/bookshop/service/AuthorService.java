package com.bookshop.service;

import com.bookshop.model.Author;

import java.util.List;

public interface AuthorService {
    List<Author> findAll();

    List<Author> findAllByName(String name);

    Author findById(Long id);

    Author save(Author author);

    Author update(Long id, String name, String surname, String description);

    void deleteById(Long id);
}
