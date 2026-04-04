package co.ke.xently.demo.books.book;

import co.ke.xently.log.mask.Mask;

public record BookDto(String title, String author, @Mask String email, String phoneNumber) {
}
