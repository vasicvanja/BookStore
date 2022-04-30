package com.bookshop.web;

import com.bookshop.model.Author;
import com.bookshop.model.Book;
import com.bookshop.model.Category;
import com.bookshop.service.AuthorService;
import com.bookshop.service.BookService;
import com.bookshop.service.CategoryService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final CategoryService categoryService;
    private final AuthorService authorService;

    public BookController(BookService bookService, CategoryService categoryService, AuthorService authorService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.authorService = authorService;
    }

    @GetMapping
    public String getBookPage(Model model) {
        List<Book> books = this.bookService.findAll();
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/new-book")
    @Secured("ROLE_ADMIN")
    public String addNewBookPage(Model model) {
        List<Category> categories = this.categoryService.findAll();
        List<Author> authors = this.authorService.findAll();

        model.addAttribute("categories", categories);
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authors);

        return "add-book";
    }

    @GetMapping("/{id}/edit")
    @Secured("ROLE_ADMIN")
    public String editBooksPage(Model model, @PathVariable Long id) {
        try {
            Book book = this.bookService.findById(id);
            List<Category> categories = this.categoryService.findAll();
            List<Author> authors = this.authorService.findAll();

            model.addAttribute("book", book);
            model.addAttribute("authors", authors);
            model.addAttribute("categories", categories);

            return "add-book";
        } catch (RuntimeException ex) {
            return "redirect:/books?error=" + ex.getMessage();
        }
    }

    @PostMapping("/save")
    @Secured("ROLE_ADMIN")
    public String saveBook(
            @Valid Book book,
            BindingResult bindingResult,
            @RequestParam MultipartFile image,
            Model model) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = this.categoryService.findAll();
            List<Author> authors = this.authorService.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("authors", authors);
            return "add-book";
        }
        try {
            this.bookService.saveBook(book, image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/books";
    }

    @PostMapping("/{id}/delete")
    @Secured("ROLE_ADMIN")
    public String deleteBook(@PathVariable Long id) {
        this.bookService.deleteById(id);
        return "redirect:/books";
    }
}