package com.bookshop.model;

import com.bookshop.model.enumerations.ShoppingCartStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ShoppingCartStatus status;

    private LocalDateTime createDate;

    private LocalDateTime closeDate;

    @ManyToOne
    private User user;

    @ManyToMany
    @JoinTable(name = "cart_books",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> books;

    public ShoppingCart() {
        this.user = user;
        books = new ArrayList<>();
        createDate =LocalDateTime.now();
        status = ShoppingCartStatus.CREATED;
    }
}
