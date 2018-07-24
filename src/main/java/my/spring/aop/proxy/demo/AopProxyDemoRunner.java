package my.spring.aop.proxy.demo;

import my.spring.aop.proxy.demo.service.DemoService;
import my.spring.aop.proxy.demo.service.NoAspectService;
import my.spring.aop.proxy.demo.service.NoTransactionalService;
import my.spring.aop.proxy.demo.service.impl.NoInterfaceDemoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

/**
 * @author Lee Tae Su
 * @version 1.0
 * @project spring-aop-demo
 * @since 2018-07-24
 */
@Service
public class AopProxyDemoRunner implements ApplicationRunner {
	@Value("${spring.aop.proxy-target-class}")
	private String aopProxyTargetClassOption;
	
	private DemoService demoService;
	private NoInterfaceDemoImpl noInterfaceDemo;
	private NoAspectService noAspectService;
	private NoTransactionalService noTransactionalService;
	
	public AopProxyDemoRunner(DemoService demoService,
							  NoInterfaceDemoImpl noInterfaceDemo, 
							  NoAspectService noAspectService, 
							  NoTransactionalService noTransactionalService) {
		this.demoService = demoService;
		this.noInterfaceDemo = noInterfaceDemo;
		this.noAspectService = noAspectService;
		this.noTransactionalService = noTransactionalService;
	}
	
	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		System.out.println("aopProxyTargetClassOption is " + aopProxyTargetClassOption);
		System.out.println("demoService is " + demoService.getClass());
		System.out.println("noInterfaceDemo is " + noInterfaceDemo.getClass());
		System.out.println("noAspectService is " + noAspectService.getClass());
		System.out.println("noTransactionalService is " + noTransactionalService.getClass());
		
		/*
		//@Value 주석 후
		aopProxyTargetClassOption is null
		demoService is class my.spring.aop.proxy.demo.service.impl.DemoServiceImpl$$EnhancerBySpringCGLIB$$329df4dc
		noInterfaceDemo is class my.spring.aop.proxy.demo.service.impl.NoInterfaceDemoImpl$$EnhancerBySpringCGLIB$$a5ca01f7
		noAspectService is class my.spring.aop.proxy.demo.service.impl.NoAspectServiceImpl$$EnhancerBySpringCGLIB$$12003ee6
		noTransactionalService is class my.spring.aop.proxy.demo.service.impl.NoTransactionalServiceImpl

		aopProxyTargetClassOption is false
		demoService is class com.sun.proxy.$Proxy90
		noInterfaceDemo is class my.spring.aop.proxy.demo.service.impl.NoInterfaceDemoImpl$$EnhancerBySpringCGLIB$$40440710
		noAspectService is class com.sun.proxy.$Proxy91
		noTransactionalService is class my.spring.aop.proxy.demo.service.impl.NoTransactionalServiceImpl
		
		aopProxyTargetClassOption is true
		demoService is class my.spring.aop.proxy.demo.service.impl.DemoServiceImpl$$EnhancerBySpringCGLIB$$e573c49b
		noInterfaceDemo is class my.spring.aop.proxy.demo.service.impl.NoInterfaceDemoImpl$$EnhancerBySpringCGLIB$$589fd1b6
		noAspectService is class my.spring.aop.proxy.demo.service.impl.NoAspectServiceImpl$$EnhancerBySpringCGLIB$$999f6b5f
		noTransactionalService is class my.spring.aop.proxy.demo.service.impl.NoTransactionalServiceImpl
		 */
		
		demoService.demo();
		noInterfaceDemo.demo();
		noAspectService.noDemo();
		
		demoService.repositoryDemo();
		noInterfaceDemo.repositoryDemo();
		/*
		Repository in DemoServiceImpl#repositoryDemo is :class com.sun.proxy.$Proxy87
		Repository in NoInterfaceDemoImpl#repositoryDemo is :class com.sun.proxy.$Proxy87
		 */
		
		System.out.println();
		
		demoService.transactionDemo();
		noInterfaceDemo.transactionDemo();
		/*
		Repository in DemoServiceImpl#transactionDemo is :class com.sun.proxy.$Proxy87
		Repository in NoInterfaceDemoImpl#transactionDemo is :class com.sun.proxy.$Proxy87
		*/
	}
}
