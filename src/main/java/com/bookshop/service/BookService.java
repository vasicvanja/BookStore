package com.bookshop.service;

import com.bookshop.model.Book;
import com.bookshop.model.Category;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BookService {

    List<Book> findAll();

    List<Book> findAllByCategoryId(Long categoryId);

    Book findById(Long id);

    Book saveBook(Book book, MultipartFile image) throws IOException;

    Book update(Long id, String name, Double price, Integer quantity, Long categoryId, MultipartFile image ) throws IOException;

    void deleteById(Long id);
}
