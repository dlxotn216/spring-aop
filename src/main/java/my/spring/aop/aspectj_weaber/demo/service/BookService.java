package my.spring.aop.aspectj_weaber.demo.service;

import lombok.AllArgsConstructor;
import my.spring.aop.aspectj_weaber.demo.domain.Book;
import my.spring.aop.aspectj_weaber.demo.domain.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by taesu on 2018-08-21.
 */
@Service
@AllArgsConstructor
public class BookService {
    private BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> saveBooks(List<Book> books) {
        return bookRepository.saveAll(books);
    }

    public void nonTransactionalMethod() {
        //..do something
        this.transactionalMethodWillBeCommited();
        this.transactionalMethodWillBeFailed();
    }

    @Transactional
    public void transactionalMethodWillBeCommited() {
        List<Book> books = bookRepository.findAll();
        books.forEach(Book::appendDateToAuthor);

        saveBooks(books);
    }

    @Transactional
    public void transactionalMethodWillBeFailed() {
        List<Book> books = bookRepository.findAll();
        books.forEach(Book::appendDateToName);

        saveBooks(books);
        if (books.size() > 0) {
            throw new IllegalStateException("사실은 취소할거야");
        }
    }

    public void nonTransactionalMethodWillCallPrivateMethod(){
        //..do something
        privateTransactionalMethod();
    }

    @Transactional
    private void privateTransactionalMethod(){
        List<Book> books = bookRepository.findAll();
        books.forEach(Book::appendDateToName);

        saveBooks(books);
        if (books.size() > 0) {
            throw new IllegalStateException("사실은 취소할거야");
        }
    }

    public void deleteAll(){
        bookRepository.deleteAll();
    }
}
