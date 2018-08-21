package my.spring.aop.aspectj_weaber.demo.service;

import my.spring.aop.aspectj_weaber.demo.AopAspectjWeaverDemoApplication;
import my.spring.aop.aspectj_weaber.demo.domain.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by taesu on 2018-08-21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AopAspectjWeaverDemoApplication.class)
public class BookServiceTest {
    @Autowired
    private BookService bookService;

    @Test
    public void 트랜잭션이아닌메소드에서_트랜잭션메소드호출시_테스트() {
        //given
        List<Book> books = bookService.saveBooks(Arrays.asList(new Book(null, "test1", "lee", "23-123-213"),
                new Book(null, "test2", "lee", "623-123-213"),
                new Book(null, "test3", "lee", "723-123-213")));

        //when
        try {
            bookService.nonTransactionalMethod();
        } catch (IllegalStateException e) {
            //then
            assertThat(bookService.findAll().size()).isEqualTo(3);

            //IllegalStateException 발생되어 name 변경 작업은 rollback 됨
            List<Book> afterTransaction = bookService.findAll();
            assertThat(afterTransaction.get(0).getName()).isEqualTo(books.get(0).getName());
            assertThat(afterTransaction.get(1).getName()).isEqualTo(books.get(1).getName());
            assertThat(afterTransaction.get(2).getName()).isEqualTo(books.get(2).getName());

            //정상 커밋된 것은 보존 됨
            assertThat(afterTransaction.get(0).getAuthor()).isNotEqualTo(books.get(0).getName());
            assertThat(afterTransaction.get(1).getAuthor()).isNotEqualTo(books.get(1).getName());
            assertThat(afterTransaction.get(2).getAuthor()).isNotEqualTo(books.get(2).getName());

            System.out.println("\nOrigin");
            books.forEach(System.out::println);
            System.out.println("\nChanged");
            afterTransaction.forEach(System.out::println);
        }
        bookService.deleteAll();
    }

    @Test
    public void private_메소드_트랜잭션_테스트(){
        //given
        List<Book> books = bookService.saveBooks(Arrays.asList(new Book(null, "test4", "lee", "12323-123-213"),
                new Book(null, "test5", "lee", "123623-123-213"),
                new Book(null, "test6", "lee", "123723-123-213")));

        //when
        try {
            bookService.nonTransactionalMethodWillCallPrivateMethod();
        } catch (IllegalStateException e) {
            //then
            assertThat(bookService.findAll().size()).isEqualTo(3);

            //IllegalStateException 발생되어 name 변경 작업은 rollback 됨
            List<Book> afterTransaction = bookService.findAll();
            assertThat(afterTransaction.get(0).getName()).isEqualTo(books.get(0).getName());
            assertThat(afterTransaction.get(1).getName()).isEqualTo(books.get(1).getName());
            assertThat(afterTransaction.get(2).getName()).isEqualTo(books.get(2).getName());

            System.out.println("\nOrigin");
            books.forEach(System.out::println);
            System.out.println("\nChanged");
            afterTransaction.forEach(System.out::println);
        }
        bookService.deleteAll();
    }
}