package com.github.ajharry69.demo.book;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class BookAssemblerTest {

    private final BookAssembler assembler = new BookAssembler();

    @Test
    void shouldWrapDtoInEntityModel() {
        var dto = new BookDto("T", "A", "e@test.com", "0712345678");

        EntityModel<BookDto> model = assembler.toModel(dto);

        assertThat(model, notNullValue());
        assertThat(model.getContent(), equalTo(dto));
    }
}
