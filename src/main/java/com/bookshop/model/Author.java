package com.bookshop.model;

import lombok.Data;
import lombok.Generated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    private String description;

    public Author() {

    }

    public Author(@NotNull String name, @NotNull String surname, String description) {
        this.name = name;
        this.surname = surname;
        this.description = description;
    }
}
