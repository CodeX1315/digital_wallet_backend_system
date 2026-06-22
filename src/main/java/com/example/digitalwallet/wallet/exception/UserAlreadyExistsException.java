package com.example.digitalwallet.wallet.exception;

import org.springframework.stereotype.Component;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
