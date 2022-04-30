package com.bookshop.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FAILED_DEPENDENCY)
public class TransactionFailedException extends RuntimeException{
    public TransactionFailedException(String userId, String message) {
        super(String.format("Transaction for user %s failed with message: %s", userId, message));
    }
}
