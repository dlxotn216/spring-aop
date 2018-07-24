## Reflection with AOP

* **학습용 프로젝트에 AOP를 적용하며**  

 최근 advanced-java 프로젝트에서 java8 in action과 관련된 복습내용을 마쳤다.  
 이전에 JPA 학습을 진행하면서 매번 학습할 때마다 패키지를 바꾸며 커밋을 새로 올렸는데  
 생각보다 나중에 참조할 때 보기가 좋지 않았다.  

 예를들어 day01에는 day01.entitymanager 패키지 였다가 day02에는 day02.mapping 처럼 패키지 이름을 바꾸는 형태로...  
 그러다보니 작성했던 소스를 나중에 참조하자나 커밋을 하나하나 조회해야했고 커밋의 상세 내용을 봐도  
 학습 내용과는 전혀 상관없는 변경점과 새로 추가된 내용이 복합적으로 보여서 깔끔해보이지 않았다.  
 
 <img src="https://raw.githubusercontent.com/dlxotn216/image/master/JPA_%EC%8A%A4%ED%84%B0%EB%94%94_%EC%95%88%EA%B9%94%EB%81%94.png"  width="800px"/> 
 <br />  
  
 처음으로 학습한 내용을 git에 정리하는 시도이었기 때문에 더 아쉬웟다.
 
 그래서 모던 자바와 관련된 정리를 할 때는 좀 더 효율적으로 정리하기를 원했고  
 아래와 같이 정리를 했다.

 <img src="https://raw.githubusercontent.com/dlxotn216/image/master/java8_%EC%A0%95%EB%A6%AC.png" width="200px" />
 
 정리한 것에 대해서는 나름 만족한다. 패키지를 나누었고 패키지 안에도 소단원 별 어느정도 예제를 쉽게 찾을 수 있게  
 구분을 해두었기 때문이다.  
 
 또한 이전에 JPA에서 소스에 대한 실행 결과를 log.info로 콘솔에 찍고 나중에 참조하기 쉽게  
 주석으로 결과를 기록하는 작업을 해두었는데 여간 귀찮은 것이 아니었다.  
 
 아래처럼 찍히면 ...사실 앞부분에 스레드 이름이나 패키지 이름, 실행시간 Logging level 등은 불필요 정보였고  
 매번 한줄씩 마우스 드래그하여 지워주었다.
 
 > 2018-06-07 22:32:33.721 DEBUG 6808 --- [  restartedMain] org.hibernate.SQL    : delete from app_user where user_key=?  
 > 2018-06-07 22:32:33.721 DEBUG 6808 --- [  restartedMain] org.hibernate.SQL    : delete from app_user where user_key=?  
 > 2018-06-07 22:32:33.722 DEBUG 6808 --- [  restartedMain] org.hibernate.SQL    : delete from app_user where user_key=?  
 > 2018-06-07 22:32:33.722 DEBUG 6808 --- [  restartedMain] org.hibernate.SQL    : delete from app_user where user_key=?  
 
 불편함을 없애고자 모던 자바에서 정리를 할 때는 System.out.println으로 로그를 찍었다.  
 하지만 주석작업은 잘 안했다, 그냥 한번 실행해보는 것이 더 편할 것 같아서..
 
 그래서 각 실행된 결과가 어떤 코드에 대한 실행인 것을 잘 나타내려고 메소드 진입점에  
 아래처럼 일일히 콘솔에 출력을 했다.  
 ```java
 @Service
 public class ModifyDate implements ApplicationRunner {
     @Override
     public void run(ApplicationArguments applicationArguments) throws Exception {
         System.out.println("\nModify date");
         LocalDate date1 = LocalDate.of(2017, 7, 17);
         System.out.println(date1);
         
         date1 = date1.withYear(2018);
         System.out.println("withYear(2018)  -> " + date1);  
         date1 = date1.withMonth(11);
         System.out.println("withMonth(11)  -> " + date1);
         
         date1 = date1.with(ChronoField.MONTH_OF_YEAR, 3);
         System.out.println("with(ChronoField.MONTH_OF_YEAR, 3)  -> " + date1);  
         date1 = date1.plusWeeks(1);
         System.out.println("plusWeeks(1)  -> " + date1);  
         
         date1 = date1.plus(2, ChronoUnit.YEARS);
         System.out.println("plus(2, ChronoUnit.YEARS)  -> " + date1);
         
         System.out.println();
     }
  }
  ```
   
  상당히 멍청해보인다. AOP를 적용하면 훨씬 효율성있고 오히려 정리도 빨랐을텐데 말이다.   
  (게으른 개발자가 되어야 했는데 라는 생각을 다시금 했다)
  
  
 * **AOP를 통해서 챕터별 로그를 남기자**
 
 앞선 경험을 통해 모던 자바 2번째 학습으로 java7 nio2를 진행하면서 AOP를 사용하기로 했다.  
 아래와 같은 예제코드를 통해서 순조로운듯 햇다. 
 
 먼저 ChapterLogger 애노테이션을 정의하고 Around advice를 통해 AOP를 걸어 실행 전후에 로그를 찍도록 한다.
 ```java
  @Retention(value = RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD})
  public @interface ChapterLogger {}
   
  @Aspect
  public class ChapterLoggerAspect {
  	
  	@Around(value = "@annotation(me.advanced.java.config.aop.ChapterLogger)")
  	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
  		System.out.println("\n==========================Start of " + joinPoint.getSignature().toShortString() + "==========================");
  		Object proceed = joinPoint.proceed();
  		System.out.println("===========================End   of " + joinPoint.getSignature().toShortString() + "==========================\n");
  		
  		return proceed;
  	}
  }
```

실제 사용은 아래와 같았다.
```java
@Service
public class AbstractPathRunner implements ApplicationRunner {
	
	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		this.abstractPathBasic();
	}
	
	@ChapterLogger
	private void abstractPathBasic() {
		Path path1 = Paths.get("C:/users/taesu/desktop");
		Path path2 = Paths.get("C:", "users/taesu/desktop");
		Path path3 = Paths.get("C:", "users", "taesu", "desktop");
		
		System.out.println(path1);
		System.out.println(path2);
		System.out.println(path3);
	}
}
```

당연히 아래와 같이 AOP는 잘 동작했다.  
==========================Start of AbstractPathRunner.abstractPathBasic()==========================  
C:\users\taesu\desktop  
C:\users\taesu\desktop  
C:\users\taesu\desktop  
===========================End   of AbstractPathRunner.abstractPathBasic()==========================  
   
   
그런데 여기서 한 가지 더 게을러지고 싶었다.  
바로 ChapterLogger를 호출하는 코드도 없애는 부분이다.  

최초 생각한 시나리오는 아래와 같았다.  
(1) 우선 아래와 같이 ChapterRunner 애노테이션을 정의한다  
 ```java
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface ChapterRunner {}
```
(2) ApplicationRunner의 run 메소드 위에 뭍인다.
```java
@Service
public class AbstractPathRunner implements ApplicationRunner {
    @ChapterRunner
    @Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
	}
}
```

(3) ChapterLogger 애노테이션 기반으로 Aronnd advice를 지정하여 AOP에서 reflection을 통해  
클래스의 모든 메소드 중 ChapterLogger 애노테이션이 붙은 메소드를 다 실행한다.
```java
@Aspect
public class ChapterRunnerAspect {
	@Autowired
	private ApplicationContext applicationContext;
	
	@Around(value = "@annotation(me.advanced.java.config.aop.ChapterRunner)")
	public Object afterReturning(ProceedingJoinPoint joinPoint) throws Throwable {
		final Class<?> clazz = Class.forName(joinPoint.getSignature().getDeclaringTypeName());
		Arrays.stream(clazz.getDeclaredMethods())
				.filter(method -> Arrays.stream(method.getDeclaredAnnotations()).anyMatch(annotation -> annotation instanceof ChapterLogger))
				.forEach(method -> {
                        try {
                            method.setAccessible(true);         //private 메소드도 실행 할 수 있도록
                            method.invoke(clazz.newInstance()); //메소드 실행 주체는 새로운 인스턴스 생성으로
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            log.error("ChapterLogger 수행 중 에러 발생", e);
                        }
                    });
		return joinPoint.proceed();
	}
}
```

하지만 아무리 실행을 해도 ChapterLogger 애노테이션에 걸린 AOP는 동작하지 않았다.  
AOP의 기본을 잊은 것인데 바로 Spring의 기본 AOP는 Proxy를 통해 동작한다는 것이다.  
(@Transactional 관련 동작 테스트를 그렇게 했는데도 깜빡햇다.)  
  
그럼 인터페이스를 구현하지 않아서 Proxy가 동작하지 않은건가?  
스프링은 jdk의 동적 proxy와 cglib 기반 proxy를 매커니즘으로 사용하는데  
언제부터인지 모르지만 대상이 interface를 구현하면 jdk 동적 proxy를 사용하고  
그렇지 않으면 cglib 기반 proxy를 사용한다. 그래서 인터페이스를 구현하지 않은 것은 원이이 아니다  

그래서 처음엔 같은 클래스 내부에서 호출하기 때문에 Proxy가 동작하지 않은 것으로 파악하여  
안일하게 아래와 같이 Aspect 코드를 바꾸었다.  
```java
@Aspect
public class ChapterRunnerAspect {
	@AfterReturning(value = "@annotation(ChapterRunner)")
    public void afterReturning(JoinPoint joinPoint) throws Throwable {
		final Class<?> clazz = Class.forName(joinPoint.getSignature().getDeclaringTypeName());
		Arrays.stream(clazz.getDeclaredMethods())
				.filter(method -> Arrays.stream(method.getDeclaredAnnotations()).anyMatch(annotation -> annotation instanceof ChapterLogger))
				.forEach(method -> {
                    try {
                        method.setAccessible(true);         //private 메소드도 실행 할 수 있도록
                        method.invoke(clazz.newInstance()); //메소드 실행 주체는 새로운 인스턴스 생성으로
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("ChapterLogger 수행 중 에러 발생", e);
                    }
                });
	}
}
```
Advice를 AfterReturning으로 바꾸었다.  하지만 마찬가지로 ChapterLogger에 걸린 AOP는 동작하지 않았다.  

왜그럴까? 바로 method.invoke(clazz.newInstance()) 부분이 문제다.  
Spring에서 AOP는 기본적으로 Proxy로 동작하고 대상 타겟은 결국 Bean 객체이다.  
다시말해서 Spring 컨테이너가 관리하는 Bean에 대한 프록시 객체를 생성하고 원본 대신 프로시를 쓰는 것이다.  

위 코드에선 Spring의 Bean이 아닌 새로운 인스턴스를 통해서 method를 실행하고 있다.  
그렇기 때문에 백날 AOP를 걸어도 ChapterLogger 애노테이션에 걸린 AOP는 동작하지 않는다.  
  
그래서 아래와 같이 Aspect 코드를 바꾸었다.  
```java
@Aspect
public class ChapterRunnerAspect {
	@Autowired
	private ApplicationContext applicationContext;
	
	@AfterReturning(value = "@annotation(ChapterRunner)")
    public void afterReturning(JoinPoint joinPoint) throws Throwable {
		final Class<?> clazz = Class.forName(joinPoint.getSignature().getDeclaringTypeName());
		Arrays.stream(clazz.getDeclaredMethods())
				.filter(method -> Arrays.stream(method.getDeclaredAnnotations()).anyMatch(annotation -> annotation instanceof ChapterLogger))
				.forEach(method -> {
                    try {
                        method.setAccessible(true);
                        method.invoke(applicationContext.getBean(clazz));   //Bean이 메소드 실행 주체
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("ChapterLogger 수행 중 에러 발생", e);
                    }
                });
	}
}
```
이제 될만도 한데 역시나 안된다.  
프록시가 제대로 동작하지 않는걸까? Debugging을 걸어보면 아래와 같이 cglib가 생성한 proxy임을 알 수 있다.  
<img src="https://raw.githubusercontent.com/dlxotn216/image/master/aop_bean_cglib%ED%99%95%EC%9D%B8.png" width="800px" />

왜그럴까? 바로 ChapterLogger 애노테이션이 붙은 메소드가 문제다.  
아래를 보면 private 메소드에 ChapterLogger 애노테이션을 붙였다.   
 ```java
 class AAA{
    @ChapterLogger
    private void abstractPathBasic() {}
}
```
스프링의 AOP는 접근 가능한 메소드에 대해서만 프록시로 동작한다   
따라서 아래와 같이 public으로 바꾸어주고 reflection에서 accessible 속성을 바꾸는 코드는 제거한다.  
```java
@Aspect
public class ChapterRunnerAspect {
	@Autowired
	private ApplicationContext applicationContext;
	
	@AfterReturning(value = "@annotation(ChapterRunner)")
    public void afterReturning(JoinPoint joinPoint) throws Throwable {
		final Class<?> clazz = Class.forName(joinPoint.getSignature().getDeclaringTypeName());
		Arrays.stream(clazz.getDeclaredMethods())
				.filter(method -> Arrays.stream(method.getDeclaredAnnotations()).anyMatch(annotation -> annotation instanceof ChapterLogger))
				.forEach(method -> {
                    try {
                        method.invoke(applicationContext.getBean(clazz));   //Bean이 메소드 실행 주체
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("ChapterLogger 수행 중 에러 발생", e);
                    }
                });
	}
}
```

결과를 보면 아주 깔끔하게 잘 동작한다.  
한 가지 이상한건 아래와 같이 setAccessible 옵션을 true로 주는 것이 제거됨에 따라
  
혹시 private 메소드에 ChapterLogger 애노테이션을 붙여 런타임 예외를 만나게 될까 싶어 방어코드를 넣었는데  
public 메소드임에도 불구하고 isAccessible 메소드에선 false를 반환했다는 점이다. 좀 더 조사가 필요할 것 같다.

```java
@Aspect
public class ChapterRunnerAspect {
	@Autowired
	private ApplicationContext applicationContext;
	
	@AfterReturning(value = "@annotation(ChapterRunner)")
    public void afterReturning(JoinPoint joinPoint) throws Throwable {
		final Class<?> clazz = Class.forName(joinPoint.getSignature().getDeclaringTypeName());
		Arrays.stream(clazz.getDeclaredMethods())
				.filter(method -> Arrays.stream(method.getDeclaredAnnotations()).anyMatch(annotation -> annotation instanceof ChapterLogger))
				.forEach(method -> {
                    try {
                            //방어코드
                        	if(method.isAccessible()){
                                method.invoke(applicationContext.getBean(clazz));	
                            }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("ChapterLogger 수행 중 에러 발생", e);
                    }
                });
	}
}
```



* **마무리** 

(1)  
사실 ChapterRunner 애노테이션을 적용하면서 ChapterLogger 애노테이션에 적용되는 AOP는 쓸모가 없다.  
아래와 같이 ChapterRunner 애노테이션이 적용 된 Aspect에서 처리가 가능하기 때문이다.  
```java
@Aspect
public class ChapterRunnerAspect {
	@Autowired
	private ApplicationContext applicationContext;
	
	@AfterReturning(value = "@annotation(ChapterRunner)")
    public void afterReturning(JoinPoint joinPoint) throws Throwable {
		final Class<?> clazz = Class.forName(joinPoint.getSignature().getDeclaringTypeName());
		Arrays.stream(clazz.getDeclaredMethods())
				.filter(method -> Arrays.stream(method.getDeclaredAnnotations()).anyMatch(annotation -> annotation instanceof ChapterLogger))
				.forEach(method -> {
                    try {
                        System.out.println("\n==========================Start of " + joinPoint.getSignature().toShortString() + "==========================");
                        method.invoke(applicationContext.getBean(clazz));                        		
                        System.out.println("===========================End   of " + joinPoint.getSignature().toShortString() + "==========================\n");
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("ChapterLogger 수행 중 에러 발생", e);
                    }
                });
	}
}
```

method.invoke() 전후에 ChapterLoggerAspect에서 하는 일을 하면 된다. 아주 간단히  
하지만 이미 적용한 애노테이션을 굳이 거두고 싶진 않았고 왜 안되는지 원인 파악을 하고 싶었기에  
관련 자료를 조사도 하고 해결하여 이렇게 정리한다.

(2)  
AOP의 동작은 대충 알지만 어떤 매커니즘이 있는지 관련 조사 및 정리가 필요할 것 같다.  
  
이전에 spring 4. 초반대를 사용할 때는 AOP가 interface를 구현한 대상에 대해 적용되고  
그렇지 않은 경우엔 별도의 cglib 라이브러리를 추가 후 proxyTargetClass=true 와 같은 설정을 해주어야  
interface를 구현하지 않은 클래스에도 AOP가 동작 했던 것으로 기억한다.  

```xml
<aop:config proxy-target-class="true"> 

출처: http://ddakker.tistory.com/280 [ddakker님의 블로그]
```

그래서 "Service 클래스는 항상 interface를 구현하도록 해야한다" 라는 관례도 있던 것으로 기억한다.

spring boot를 사용하고 있어서인가 잠시 살펴보았지만 아래와 같이 default는 false이다.  
```java
public @interface EnableAspectJAutoProxy {
	/**
	 * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed
	 * to standard Java interface-based proxies. The default is {@code false}.
	 */
	boolean proxyTargetClass() default false;

	/**
	 * Indicate that the proxy should be exposed by the AOP framework as a {@code ThreadLocal}
	 * for retrieval via the {@link org.springframework.aop.framework.AopContext} class.
	 * Off by default, i.e. no guarantees that {@code AopContext} access will work.
	 * @since 4.3.1
	 */
	boolean exposeProxy() default false;
}
```

아마 AOP, Aspect, Weaver 등 관련 용어 및 개념 정리가 필요한 것 같다.  
아래 블로그가 글을 잘 설명하고 있는 것 같다  
<a href="https://blog.outsider.ne.kr/845">[Spring 레퍼런스] 8장 스프링의 관점 지향 프로그래밍 #3</a>