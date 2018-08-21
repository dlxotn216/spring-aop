package my.spring.aop.aspectj_weaber.demo.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by taesu on 2018-08-21.
 */
public interface BookRepository extends JpaRepository<Book, Long> {
}
