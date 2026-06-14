package co.ke.xently.demo.books.book;

import co.ke.xently.log.mask.NoLogForging;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String email;
    private String phoneNumber;

    @NoLogForging
    public String getTitle() {
        return title;
    }
}
