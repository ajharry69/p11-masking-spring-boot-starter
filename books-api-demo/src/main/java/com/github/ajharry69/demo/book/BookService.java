package com.github.ajharry69.demo.book;

import com.github.ajharry69.demo.book.exceptions.BookNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(BookService::getBookDto);
    }

    public BookDto createBook(BookDto book) {
        log.info("Creating book: {}", book);
        var bookEntity = Book.builder()
                .title(book.title())
                .email(book.email())
                .author(book.author())
                .phoneNumber(book.phoneNumber())
                .build();
        var savedBook = bookRepository.save(bookEntity);
        log.info("Created book: {}", savedBook);
        return getBookDto(savedBook);
    }

    public BookDto  getBookById(Long id) {
        log.info("Getting book by id: {}", id);
        var book = bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);
        log.info("Found book: {}", book);
        return getBookDto(book);
    }

    public BookDto updateBook(Long id, BookDto updatedBook) {
        log.info("Updating book with id: {} to: {}", id, updatedBook);
        var existingBook = bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);

        existingBook.setTitle(updatedBook.title());
        existingBook.setAuthor(updatedBook.author());
        existingBook.setEmail(updatedBook.email());
        existingBook.setPhoneNumber(updatedBook.phoneNumber());
        var book = bookRepository.save(existingBook);
        log.info("Updated book: {}", book);
        return getBookDto(book);
    }

    private static BookDto getBookDto(Book book) {
        return new BookDto(book.getTitle(), book.getAuthor(), book.getEmail(), book.getPhoneNumber());
    }

    public void deleteBook(Long id) {
        log.info("Deleting book with id: {}", id);
        bookRepository.deleteById(id);
        log.info("Deleted book with id: {}", id);
    }
}
