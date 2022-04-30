package com.bookshop.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AccountSuspendedException extends RuntimeException {
    public AccountSuspendedException() {
        super(String.format("You can't make a purchase because your account is suspended"));
    }
}