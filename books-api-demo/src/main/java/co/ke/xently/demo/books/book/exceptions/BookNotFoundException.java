package co.ke.xently.demo.books.book.exceptions;

import co.ke.xently.demo.books.exceptions.KCBException;
import org.springframework.http.HttpStatus;

public class BookNotFoundException extends KCBException {
    public BookNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Book not found");
    }
}
