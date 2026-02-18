package com.github.ajharry69.demo.book;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public class BookAssembler implements RepresentationModelAssembler<BookDto, EntityModel<BookDto>> {
    @Override
    public EntityModel<BookDto> toModel(BookDto book) {
        return EntityModel.of(
                book
        );
    }
}
