## Aspectj weaver의 LoadTimeWeaving을 이용한 Spring AOP 기반 Transaction 설정

### 들어가기에 앞서...
최근 회사의 솔루션의 코드르 보며 아래와 같은 코드를 보앗다.    
(나름 매출의 대부분을 차지하는 솔루션이다)  
<img src="https://raw.githubusercontent.com/dlxotn216/image/master/spring-aop/AOP%20%EB%AC%B8%EC%A0%9C%20%EC%BD%94%EB%93%9C.png" width="800" />  
  
private 메소드에 @Transactional 어노테이션을 선언하였고 propagation 설정을 REQUIRES_NEW로 주었다.    
자칫 AOP에 대해서 얕은 지식이 있는 사랑미 보기엔 아래 처럼 생각 할 수 있다.  
> private 메소드에 @Transactional이 있네? 잘 동작하지 않는것 아닌가?  
아~ REQUIRES_NEW 옵션이 뭔가 처리를 해주나?  찾아보니 무조건 새로운 트랜잭션을 만든다는데... 동작하는건가보지 뭐~

당연히 REQUIRES_NEW 옵션은 propagation 즉, 트랜잭션의 전파 옵션이다.  
@Transactional 메소드가 아닌 메소드에서 위의 private @Transactional 메소드를 호출하였다면  
그 서비스의 트랜잭션은 제대로 동작하지 않을 것이며, 실제로 솔루션의 많은 코드에서  
@Transactional 메소드가 아닌 메소드들이 private @Transactional 메소드를 호출하고 있었다.

그렇기에 수많은 곳에서 private @Transactional 메소드를 호출하고 잇던 것이겠지...

> 주제에서 벗어나지만 한가지 더 특이한 것은 private @Transactional 메소드 위의 @Transactional 메소드도  
propagation 옵션이 REQUIRES_NEW인데 그 메소드가 사용되는 곳은 모두 Controller 였던 것.  
이런 케이스에서는 굳이 propagation 옵션이 REQUIRES_NEW일 필요가 없을 것인데 말이다.

좀 더 많은 분들이 Spring의 사용법만 대충 알지 않고  
적어도 AOP, Transactional, Async 같은 강력하지만 잘 모르고 쓰면 위험한 것들에 대한  
내부 동작에 대해서 파악했으면 하는 바람에 그리고 나도 배운것을 잊지 않기 위해 정리해보았다.

## 1. 프로젝트 설정
### (1) Maven  
Aspectj weaver의 LoadTimeWeaving을 사용하기 위해선 아래 두가지 dependency가 필요한다  
* spring-instrument  
* aspectjweaver  

aspectj-weaver는 spring-boot-starter-aop안에 포함되어있다.  
<img src="https://raw.githubusercontent.com/dlxotn216/image/master/spring-aop/spring-boot-starter-aop-aspectj-weaver2.png" />  

따라서 아래와 같이 Spring boot 기반의 설정에 spring-instrument dependency를 추가하여 설정한다
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.springframework/spring-instrument -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-instrument</artifactId>
        <version>5.0.7.RELEASE</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### (2) SpringBootConfiguration  
아래와 같이 Transaction 설정을 Aspectj를 이용하도록 설정 후  
Aspectj의 LoadTimingWeaving 설정을 활성화 한다
```java
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@EnableLoadTimeWeaving
public class AopAspectjWeaverDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(AopAspectjWeaverDemoApplication.class);
    }
}

```

### (3) 실행 옵션 설정  
Intellij 기준 VM Option에 아래 argument를 추가한다
(JUnit Test를 실행한다면 각 Test의 VM Options에도 별도로 기재해주어야 한다) 
 
-javaagent:C:\Users\taesu\.m2\repository\org\springframework\spring-instrument\5.0.7.RELEASE\spring-instrument-5.0.7.RELEASE.jar  
-javaagent:C:\Users\taesu\.m2\repository\org\aspectj\aspectjweaver\1.8.13\aspectjweaver-1.8.13.jar  

<img src="https://raw.githubusercontent.com/dlxotn216/image/master/spring-aop/aspectj-weaver-vmoption.png" width="800" height="500" />

## 2. Aspectj weaver 기반 Transactional 이란?

### (1) Proxy 기반 Spring AOP   
기본적으로 Spring Transaction은 Spring AOP를 기반으로 하고 있으며    
Spring AOP는 Proxy기반으로 동작한다  
(참조 <a href="https://github.com/dlxotn216/spring-aop/tree/master/src/main/java/my/spring/aop/proxy/demo">Spring AOP의 동작</a>)

따라서 Spring AOP 기반의 Transactional 설정은 아래와 같은 제약이 있다.  
(1) 동일 클래스에서 @Transactional이 선언되지 않은 메소드에서 @Transactional이 선언된 메소드를 호출하여도 트랜잭션이 동작하지 않음    
(2) @Transactional이 선언된 private 메소드에는 트랜잭션이 동작하지 않음    
(3) proxy-target-class 옵션을 true로 설정하지 않으면 interface를 구현하지 않는 클래스에 선언 된 @Transactional 메소드는 동작하지 않음    
> 최신 버전의 Spring boot에선 proxy-target-class 옵션을 대부분 true로 설정하여  
  JDK dynamic proxy를 사용하는 것이 아닌 CGLIB proxy를 사용한다.  
  따라서 interface를 구현하지 않은 클래스에 선언된 @Transactional 메소드이더라도 잘 동작한다

### (2) Aspectj weaver의 LoadTimeWeaving을 이용한 Transactional 테스트  
Aspectj weaver의 LoadTimeWeaving을 이용하면 앞선 제약을 해결할 수 있다.

아래와 같이 매우 난잡해보이는 서비스가 있다라고 가정해보자
```java
@Service
@AllArgsConstructor
public class BookService {
    private BookRepository bookRepository;

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

}
```

아래의 테스트를 보면 @Transactional 메소드가 아닌 메소드가 최초 호출 되고  
그 메소드에서 두 가지 작업을 처리하는 @Transactional 메소드를 호출하고있다.  

하나는 Author field에 현재 년월일을 더하여 저장하는 작업이고  
하나는 Book name field에 현재 년월일을 더하여 저장 후 IllegalStateException을 발생시키는 작업이다.

Spring AOP를 이용했다면 당연히 IllegalStateException이 발생한 작업의 Rollback은 이뤄지지 않을 것이고 아래 테스트는 깨진다.  
하지만 Aspectj weaver를 이용하면 아래 테스트는 성공한다.
```java
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
}
```

이래 결과를 보면 정상 커밋 된 작업의 결과로 author의 뒤에 년월일이 붙어있다.  
또한 name 뒤에 년월일을 붙인후 저장하였지만 IllegalStateException을 발생시켜 Rollback 시킨 작업의 결과로  
name은 기존과 동일한 값을 가지고 있다.  
따라서 @Transactional 메소드가 아닌 메소드에서 @Transactional 메소드를 호출하여도 트랜잭션이 정상 작동했음을 알 수 있다.
> ------------결과-----------------   
  Origin  
  Book(bookKey=1, name=test1, author=lee, isbn=23-123-213)  
  Book(bookKey=2, name=test2, author=lee, isbn=623-123-213)  
  Book(bookKey=3, name=test3, author=lee, isbn=723-123-213)  
  Changed  
  Book(bookKey=1, name=test1, author=lee_2018-08-22, isbn=23-123-213)  
  Book(bookKey=2, name=test2, author=lee_2018-08-22, isbn=623-123-213)  
  Book(bookKey=3, name=test3, author=lee_2018-08-22, isbn=723-123-213)


그렇다면 private @Transactional method에 대해서도 테스트 해보자.    
마찬가지로 아래와 같은 난잡한 서비스가 있다고 가정해보자  

@Transactional 메소드가 아닌 메소드(nonTransactionalMethodWillCallPrivateMethod)에서  
name field 뒤에 년월일을 붙여 저장 후 IllegalStateException을 발생시키는 작업을 처리하는    
private @Transactional 메소드(privateTransactionalMethod)를 호출하였다.  
```java
package my.spring.aop.aspectj_weaber.demo.service;

import lombok.AllArgsConstructor;
import my.spring.aop.aspectj_weaber.demo.domain.Book;
import my.spring.aop.aspectj_weaber.demo.domain.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class BookService {
    private BookRepository bookRepository;

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
```

아래의 테스트를 수행해보자.
```java
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AopAspectjWeaverDemoApplication.class)
public class BookServiceTest {
    @Autowired
    private BookService bookService;
    
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
```
결과를 확인 해보면 privateTransactionalMethod 메소드에서 수행 한  
name 뒤에 년월일을 추가하는 작업이 Rollback 되었다.   
따라서 private method에도 트랜잭션이 제대로 동작 했음을 알 수 있다.

> 결과  
Origin  
Book(bookKey=4, name=test4, author=lee, isbn=12323-123-213)  
Book(bookKey=5, name=test5, author=lee, isbn=123623-123-213)  
Book(bookKey=6, name=test6, author=lee, isbn=123723-123-213)  
Changed  
Book(bookKey=4, name=test4, author=lee, isbn=12323-123-213)  
Book(bookKey=5, name=test5, author=lee, isbn=123623-123-213)  
Book(bookKey=6, name=test6, author=lee, isbn=123723-123-213)  

### 3. 마무리
Spring의 AOP를 정리하면서 Aspectj weaver를 이용한 설정 및 테스트를 정리해보았다.  
Proxy 기반의 AOP에서는 Method 대상의 JoinPoint를 설정할 수 있으나  
Aspectj waver 방식을 이용하면 필드의 값 변경과 같은 JoinPoint를 사용할 수 있다.  

매우 강력하고 유연한 기능이지만 실제 프로젝트에서 사용하기엔 위함성이 어느정도 있을 것 같은 것이
범용적으로 Spring에서 AOP는 Proxy 기반의 Spring AOP를 사용하고 있다.   

따라서 private method에 @Transactional을 선언하였거나 @Transactional 메소드가 아닌 메소드에서  
@Transactional 메소드를 호출하였거나 하는 등의 코드는  리뷰어가 그 프로젝트의 dependency, configuration을   
일일히 살펴보고 코드리뷰를 진행하지 않은 이상 코드리뷰 대상이 될 것이므로 혼란이 클 것 같다.  
  
또한 JUnit Test를 수행할 때마다 VM Option을 입력 해주어야 한다... default 설정이 Intellij에서 지원 하는지는 모르겠지만.  

결정적으로 일반적인 아키텍처에서 private method에서 개별 트랜잭션으로 처리할 일이 있을까?  
@Transactional이 아닌 메소드에서 호출하는 메소드들 중 독립 트랜잭션으로 처리해야할 일이 있을까?  
이런 경우엔 대부분 아키텍처가 꼬였을 것이라고 생각한다.   

굳이 Aspectj weaver를 통해 트랜잭션을 제대로 동작하게 하려기보다는  
오히려 private method가 잘못 된 책임을 가지고 있지는 않은 지,  혹은 개별 서비스로 분리해야 하는 것은 아닌 지,  
트랜잭션의 범위가 너무 큰 것은 아닌 지 등의 확인을 통해 리팩토링을 하는 것이 우선인 것 같다. 

  
 