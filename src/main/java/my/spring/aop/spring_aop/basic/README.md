# Spring AOP
Spring Framework의 3가지 특징(AOP, PSA, DI/IOC) 중 하나인 AOP에 대해 기본 개념을 다룬다.

## 1. AOP란?
#### (1) 개념
AOP는 Aspect-Oriented Programming의 약자로 관점지향 프로그래밍을 뜻한다.  

OOP에선 객체를 기반으로하여 객체와 객체간의 상호작용을 중심사로 프로그래밍을 한다. 반면 AOP에서는 Application을  
종단 관심사(핵심 로직)와 횡단 관심사(공통 로직)로 분리하여 주요 비즈니스 로직은 종단 관심사로 분류하고  
Application의 전반적인 공통기능은 횡단 관심사로 분류한다.  
 
횡단 관심(Cross Cutting Concern)의 대표적인 예는 Logging, 권한 검사, Transaction 등이 있으며 대부분의 Application에서  
필수적으로 요구하는 사항들이다. 

#### (2) 용어  
- 관점(Aspect): 여러 클래스에 걸친 관심사의 모듈화.  
- 조인 포인트(Join point): 메소드의 실행이나 에외처리같은 실행 중의 특정 한 지점을 말한다.  
  (Spring의 AOP는 Proxy 기반이므로 항상 메소드의 실행을 의미한다)
- 어드바이스(Advice): 특정 Join Point에서 Aspect가 취하는 행동을 말하며 around, before, after 등  
  여러 가지 type의 Advice가 있다. 
- 포인트 컷(Pointcut): Join Point를 매칭하는 것으로 Advice는 Pointcut 표현식과 연결되며  
  Pointcut에 매칭되는 곳에서 실행된다.  
- 인트로덕션(Introduction): 타입을 대신하여 메소드나 필드를 추가적으로 선언한다.  
- 대상 객체(target object): 하나 이상의 관점으로 Advice된 객체를 말한다.  
- AOP Proxy: Spring의 AOP는 Proxy 기반으로 동작하며 그 종류는 JDK Dynamic proxy, CGLIB가 존재한다.  
- 위빙(Weaving): 다른 Type이나 Advice 된 객체를 생성하여 Aspect와 연결한다.  
  Compile-time, Load-time, Run-time 시점에서 수행될 수 있다.   
  (Spring AOP는 Run-time Weaving을 수행한다)
  
  
※ Advice의 Type
* Before
* After returning
* After throwing
* After
* Around

 ## 2 Spring에서의 AOP 동작 방식의 종류와 특징
 #### (1) Spring AOP Proxy 기반  
 Spring에서 기본적으로 제공하는 AOP의 동작방식이다. 대상객체에 대한 Proxy를 생성하여 처리한다.  
 JDK Dynamic Proxy는 Java의 리플랙션을 통해 대상객체에 대한 Proxy를 만든다. 반면 CGLIB는  
 Byte code를 조작하여 Proxy를 만든다.
 
 Spring에선 대상 객체가 Interface를 구현하고 있다면 JDK Dynamic proxy를 사용하였고  
 그렇지 않다면 CGLIB를 사용하였다. (이 경우엔 설정에서 proxy-target-class 옵션을 true로 명시해야한다.)  
 
 하지만 Spring Boot에선 이야기가 다르다. 기본적으로 proxy-target-class 옵션을 true로 설정하고 있어  
 JDK Dynamic Proxy 대신 CGLIB를 사용한다. 
 
 그 이유는 CGLIB가 JDK Dynamic Proxy보다 이전보다 더욱 나은 성능을 보이고 있고 문제를 일으킬  
 가능성이 더욱 적어서라고 한다.
 
 Proxy 기반이기때문에 제약사항이 어느정도 있다. 대표적으로 Transaction을 예로들면,  
 @Transactional 메소드가 아닌 메소드에서 @Transactional 메소드를 호출한 경우  
 트랜잭션 처리가 안되는 문제가 있다.  
 
 만약 Controller에서 위 처럼 메소드를 호출한 경우 처음 호출되는 메소드는 @Transactional 메소드가  
 아니므로 Proxy 객체를 만들지 않게된다. 또한 동일 클래스 내에서 아무리 메소드를 호출해도 Proxy 객체는 생성될 수 없다.  
 
 또한 Proxy는 private 메소드에 대해 적용될 수 없으므로 private 메소드에 @Transactional을 백날 선언해봤자  
 트랜잭션은 제대로 처리될 수 없다는 문제가 있다. 
  
 #### (2) Aspectj 기반
 AspectJ는 대표적인 AOP 프레임워크이다. 핵심 라이브러리로 Aspectj-Weaver를 지원하며 총 2가지 유형의 Weaving을  
 제공한다.
  
 Load-Time Weave(LTW)는 객체를 Load할 때, Aspectj에 의해 Weaving 된 객체를 넘기는 방식이다.  
 Spring을 예로 들면 Application context에 Load 된 Bean이 Loading될 때 Weaving 된 객체를 생성하여  
 Load하는 방식이다. 당연히 Load time에 Weaving을 진행하므로 약간의 성능상의 이슈가 있다.  
 Container에서 실행시 LTW를 위한 설정이 필요한 단점도 있다. 
 
 Compile-Time Weaver(CTW)는 Load 타임이 아닌 Compile-Time에 Weaving 된 객체를 생성하는 방식이다.  
 하지만 Lombok과 같이 Compile 시에 간섭하는 다른 라이브러리와 충돌 가능성이 있고  
 내가 구현한 결과물(class 파일)을 직접 조작하기 때문에 오동작이 발생하는 버그가 나타나면 발견하기 매우 힘들다.  
 또한 Maven으로 빌드 시 별도의 설정을 통해 컴파일러 설정을 적용해주어야 하는데 설정법에 대한 레퍼런스가 적고  
 에러도 자주 마주할 수 있어 설정이 힘든 단점이 있다.  
 
 
 ## 3. 마치며
 AOP에 대해 잊고 있었던 부분, 몰랐던 부분들을 다시 정리할 수 있는 좋은 기회이었던 것 같다.  
 AspectJ weaver를 통해 AOP를 처리하도록 하기 위해 여러 설정법들을 찾아보고 테스트 해보았지만  
 확실히 개발 생산성이나 통상적인 실행흐름 등을 생각했을 땐 Spring의 proxy 기반 AOP를 사용하는 것이 좋다고 생각이 든다.  
 
 매번 AOP 관련 기능을 개발할 때마다 Pointcut 표현식을 찾아보는 것이 귀찮아 이참에 잘 정리해볼까 싶었지만  
 굳이 외워야 할 필요가 있을까 생각이 들어 그만 두었다. 
 
 개인적으로 정규표현식과 같이 Pattern matching을 기반으로 하는  
 개발습관은 좋지 않다고 생각이 든다. 매우 강력한 도구이긴 하지만 나중에 유지보수 할 사람을 생각하면 조금 더  
 명시적인 방법을 택하는 것이좋다고 생각한다.  
 
 그렇기에 AOP의 Pointcut 표현식 특히나 and, or 등으로 조합할 수 있는 것이 매우 강력하긴 하지만 장황하게 사용하는 것  
 (흔히 말해 살벌하게....)은 지양해야 한다고 생각이 든다. 명시적으로 Annotation을 사용하면 좋지 않을까?  
 괜히 AOP 때문에 Naming, Package 구조 등에 대해 규약이 생겨버리면 곤란할 뿐더러 후에 누군가가 Naming, Package 관련  
 Refactoring을 했을 때 발생하는 버그 때문에 고생하는 억울함을 덜기 위해서말이다. 
 
 > 통상적인 실행흐름이란 예컨데 private 메소드에 @Transactional 선언을 한 경우 트랜잭션이 제대로 동작하지 않는 것  
 > 즉, 일반적인 Spring application에서 동작하는 흐름을 의도한 문맥이다.
 
 