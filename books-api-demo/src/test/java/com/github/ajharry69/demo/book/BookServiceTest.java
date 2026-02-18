package com.github.ajharry69.demo.book;

import com.github.ajharry69.demo.book.exceptions.BookNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookServiceTest {

    private final BookRepository bookRepository = Mockito.mock(BookRepository.class);
    private final BookService service = new BookService(bookRepository);

    @Test
    void shouldGetAllBooks() {
        var book1 = Book.builder().id(1L).title("T1").author("A1").email("e1@test.com").phoneNumber("0712345678").build();
        var book2 = Book.builder().id(2L).title("T2").author("A2").email("e2@test.com").phoneNumber("0712345679").build();
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<BookDto> list = service.getAllBooks();

        assertThat(list.size(), is(2));
        assertThat(list, contains(
                new BookDto("T1", "A1", "e1@test.com", "0712345678"),
                new BookDto("T2", "A2", "e2@test.com", "0712345679")
        ));
    }

    @ParameterizedTest(name = "shouldCreateBookFromDto: {0},{1}")
    @CsvSource({
            "Title, Author",
            "Another, Someone"
    })
    void shouldCreateBookFromDto(String title, String author) {
        var input = new BookDto(title, author, "john.doe@test.com", "0712345678");
        var saved = Book.builder().id(10L).title(title).author(author).email(input.email()).phoneNumber(input.phoneNumber()).build();
        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        BookDto result = service.createBook(input);

        var captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        Book toSave = captor.getValue();

        assertThat(toSave.getId(), nullValue());
        assertThat(result, equalTo(new BookDto(title, author, input.email(), input.phoneNumber())));
    }

    @Test
    void shouldGetBookByIdOrThrow() {
        var stored = Book.builder().id(2L).title("T").author("A").email("e@test.com").phoneNumber("0712345678").build();
        when(bookRepository.findById(2L)).thenReturn(Optional.of(stored));

        BookDto found = service.getBookById(2L);

        assertThat(found, equalTo(new BookDto("T", "A", "e@test.com", "0712345678")));
    }

    @Test
    void shouldThrowWhenBookNotFoundById() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> service.getBookById(99L));
    }

    @Test
    void shouldUpdateExistingBook() {
        var existing = Book.builder().id(5L).title("Old").author("Auth").email("old@test.com").phoneNumber("0700000000").build();
        when(bookRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        var update = new BookDto("New", "Auth2", "new@test.com", "0711111111");
        BookDto updated = service.updateBook(5L, update);

        assertThat(updated, equalTo(update));
        assertThat(existing.getTitle(), equalTo("New"));
        assertThat(existing.getAuthor(), equalTo("Auth2"));
        assertThat(existing.getEmail(), equalTo("new@test.com"));
        assertThat(existing.getPhoneNumber(), equalTo("0711111111"));
    }

    @Test
    void shouldDeleteBookById() {
        service.deleteBook(7L);
        verify(bookRepository).deleteById(7L);
    }
}
