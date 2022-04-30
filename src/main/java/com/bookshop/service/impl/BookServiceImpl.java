package com.bookshop.service.impl;

import com.bookshop.model.Book;
import com.bookshop.model.Category;
import com.bookshop.model.exceptions.BookNotFoundException;
import com.bookshop.model.exceptions.CategoryNotFoundException;
import com.bookshop.repository.BookRepository;
import com.bookshop.repository.CategoryRepository;
import com.bookshop.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookServiceImpl(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Book> findAll() {
        return this.bookRepository.findAll();
    }

    @Override
    public List<Book> findAllByCategoryId(Long categoryId) {
        return this.bookRepository.findAllByCategoryId(categoryId);
    }

    @Override
    public Book findById(Long id) {
        return this.bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Override
    public Book saveBook(Book book, MultipartFile image) throws IOException {
        return getFinalBookWithImage(book, image);
    }

    private Book getFinalBookWithImage(Book book, MultipartFile image) throws IOException {
        if (image != null && !image.getName().isEmpty()) {
            byte[] bytes = image.getBytes();
            String base64Image = String.format("data:%s;base64,%s", image.getContentType(), Base64.getEncoder().encodeToString(bytes));
            book.setImage(base64Image);
        }
        return this.bookRepository.save(book);
    }

    @Override
    public Book update(Long id, String name, Double price, Integer quantity, Long categoryId, MultipartFile image) throws IOException {
        Book book = this.findById(id);
        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        book.setName(name);
        book.setPrice(price);
        book.setQuantity(quantity);
        book.setCategory(category);
        return getFinalBookWithImage(book, image);
    }

    @Override
    public void deleteById(Long id) {
        this.bookRepository.deleteById(id);
    }
}
