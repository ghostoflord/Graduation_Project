package com.vn.capstone.util.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Tự động trả 404 nếu bị throw
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}