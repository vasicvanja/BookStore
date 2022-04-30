package com.bookshop.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Double price;

    @NotNull
    @Min(value = 0, message = "Can't have negative quantity")
    private Integer quantity;

    @ManyToOne
    private Category category;

    @Lob
    private String image;

    @ManyToMany
    @JoinTable(name = "authors_books",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    private List<Author> authors;

    public Book() {
        this.authors = new ArrayList<>();
    }

    public Book(@NotNull String name, @NotNull Double price,
                @NotNull @Min(value = 0, message = "Can't have negative quantity") Integer quantity,
                Category category) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public Book(@NotNull String name, @NotNull Double price,
                @NotNull @Min(value = 0, message = "Can't have negative quantity") Integer quantity, Category category,
                @NotNull String image, List<Author> authors) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.image = image;
        this.authors = authors;
    }
}
