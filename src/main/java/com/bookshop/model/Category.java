package com.bookshop.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String description;

    @OneToMany
    private List<Book> books;

    public Category() {

    }

    public Category(@NotNull String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category(@NotNull String name, String description, List<Book> books) {
        this.name = name;
        this.description = description;
    }
}
