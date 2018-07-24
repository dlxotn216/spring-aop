package my.spring.aop.proxy.demo;

import my.spring.aop.proxy.demo.config.DemoAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author Lee Tae Su
 * @version 1.0
 * @project spring-aop-demo
 * @since 2018-07-24
 */
@SpringBootApplication
public class AopProxyDemoApplication {
	
	@Bean
	public DemoAspect demoAspect() {
		return new DemoAspect();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(AopProxyDemoApplication.class);
	}
}
