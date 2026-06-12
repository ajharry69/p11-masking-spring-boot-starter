package co.ke.xently.demo.books.book;

import co.ke.xently.log.mask.Mask;
import co.ke.xently.log.mask.NoLogForging;

@NoLogForging
public record BookDto(String title, String author, @Mask String email, String phoneNumber) {
}
