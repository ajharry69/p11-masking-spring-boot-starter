package com.github.ajharry69.demo.book;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/api/v1/books", produces = {MediaTypes.HAL_JSON_VALUE})
public class BookController {
    private final BookService bookService;
    private final PagedResourcesAssembler<BookDto> pagedBookAssembler;
    
    @GetMapping
    public PagedModel<EntityModel<BookDto>> getAllBooks(Pageable pageable) {
        var books = bookService.getAllBooks(pageable);
        return pagedBookAssembler.toModel(
                books,
                new BookAssembler()
        );
    }

    @PostMapping
    public ResponseEntity<EntityModel<BookDto>> createBook(@RequestBody BookDto book) {
        var createdBook = bookService.createBook(book);
        var bookAssembler = new BookAssembler();
        var model = bookAssembler.toModel(createdBook);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<BookDto>> getBookById(@PathVariable Long id) {
        var book = bookService.getBookById(id);
        var bookAssembler = new BookAssembler();
        var model = bookAssembler.toModel(book);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<BookDto>> updateBook(@PathVariable Long id, @RequestBody BookDto book) {
        var updatedBook = bookService.updateBook(id, book);
        var bookAssembler = new BookAssembler();
        var model = bookAssembler.toModel(updatedBook);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

}
