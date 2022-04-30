package com.bookshop.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookAlreadyInWishListException extends RuntimeException {
    public BookAlreadyInWishListException(String bookName) {
        super(String.format("Book %s is already in the wish list", bookName));
    }
}