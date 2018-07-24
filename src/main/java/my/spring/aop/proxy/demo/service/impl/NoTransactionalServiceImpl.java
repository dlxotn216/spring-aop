package my.spring.aop.proxy.demo.service.impl;

import my.spring.aop.proxy.demo.service.NoTransactionalService;
import org.springframework.stereotype.Service;

/**
 * @author Lee Tae Su
 * @version 1.0
 * @project spring-aop-demo
 * @since 2018-07-24
 */
@Service
public class NoTransactionalServiceImpl implements NoTransactionalService {
	
	@Override
	public void noTransactional(){
		System.out.println("NoTransactionalServiceImpl#noTransactional called");
	}
}
