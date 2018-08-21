package my.spring.aop.aspectj_weaber.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by taesu on 2018-08-21.
 */
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOOK_SEQ")
    @SequenceGenerator(name = "BOOK_SEQ", sequenceName = "BOOK_SEQ")
    private Long bookKey;

    private String name;

    private String author;

    @Column(unique = true, nullable = false)
    private String isbn;

    public void appendDateToName() {
        if (this.name != null) {
            this.name = this.name + "_" + LocalDate.now();
        }
    }

    public void appendDateToAuthor() {
        if (this.author != null) {
            this.author = this.author + "_" + LocalDate.now();
        }
    }
}
