package com.bookshop.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class WishListIsNotActiveException extends RuntimeException {
    public WishListIsNotActiveException(String username) {
        super(String.format("User with username: %s has no active wish list exception!", username));
    }
}
