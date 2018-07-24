package my.spring.aop.proxy.demo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author Lee Tae Su
 * @version 1.0
 * @project spring-aop-demo
 * @since 2018-07-24
 */
@Aspect
public class DemoAspect {
	
	@Pointcut(value = "within(my.spring.aop.proxy.demo.service..*)")
	public void projectPackagePointCut(){}
	
	@Pointcut(value = "execution(* demo*(..))")
	public void demoPointCut(){}
	
	@Pointcut(value = "projectPackagePointCut() && demoPointCut()")
	public void demoForProjectPointCut(){}
	
	@Around("demoForProjectPointCut()")
	public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		System.out.println("\nonDemo opened");
		Object proceed = proceedingJoinPoint.proceed();
		System.out.println("onDemo closed\n");
		return proceed;
	}
}
