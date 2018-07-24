package my.spring.aop.proxy.demo.service.impl;

import my.spring.aop.proxy.demo.domain.ProxyDemo;
import my.spring.aop.proxy.demo.domain.ProxyDemoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lee Tae Su
 * @version 1.0
 * @project spring-aop-demo
 * @since 2018-07-24
 */
@Service
public class NoInterfaceDemoImpl {
	private ProxyDemoRepository repository;
	
	public NoInterfaceDemoImpl(ProxyDemoRepository repository) {
		this.repository = repository;
	}
	
	public void demo() {
		System.out.println("NoInterfaceDemoImpl class method demo() called");
	}
	
	public void repositoryDemo(){
		System.out.println("Repository in NoInterfaceDemoImpl#repositoryDemo is :"+ repository.getClass());
	}
	
	@Transactional
	public void transactionDemo() {
		System.out.println("Repository in NoInterfaceDemoImpl#transactionDemo is :"+ repository.getClass());
		repository.save(new ProxyDemo());
	}
}
