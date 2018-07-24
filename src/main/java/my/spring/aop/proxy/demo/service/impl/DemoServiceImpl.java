package my.spring.aop.proxy.demo.service.impl;

import my.spring.aop.proxy.demo.domain.ProxyDemo;
import my.spring.aop.proxy.demo.domain.ProxyDemoRepository;
import my.spring.aop.proxy.demo.service.DemoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lee Tae Su
 * @version 1.0
 * @project spring-aop-demo
 * @since 2018-07-24
 */
@Service
public class DemoServiceImpl implements DemoService {
	private ProxyDemoRepository repository;
	
	public DemoServiceImpl(ProxyDemoRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public void demo() {
		System.out.println("DemoServiceImpl class method demo() called");
	}
	
	@Override
	public void repositoryDemo(){
		System.out.println("Repository in DemoServiceImpl#repositoryDemo is :"+ repository.getClass());
	}
	
	@Transactional
	@Override
	public void transactionDemo(){
		System.out.println("Repository in DemoServiceImpl#transactionDemo is :"+ repository.getClass());
		repository.save(new ProxyDemo());
	}
}
