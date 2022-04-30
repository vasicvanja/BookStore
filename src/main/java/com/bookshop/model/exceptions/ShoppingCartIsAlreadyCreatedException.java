package com.bookshop.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ShoppingCartIsAlreadyCreatedException extends RuntimeException{
    public ShoppingCartIsAlreadyCreatedException(String userId){
        super(String.format("User with username: %s already has an active shopping cart", userId));
    }
}
