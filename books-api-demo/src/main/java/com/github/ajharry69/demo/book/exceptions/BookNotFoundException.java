package com.github.ajharry69.demo.book.exceptions;

import com.github.ajharry69.demo.exceptions.KCBException;
import org.springframework.http.HttpStatus;

public class BookNotFoundException extends KCBException {
    public BookNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Book not found");
    }
}
