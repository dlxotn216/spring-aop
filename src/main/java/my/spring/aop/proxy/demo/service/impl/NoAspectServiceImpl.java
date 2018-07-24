package my.spring.aop.proxy.demo.service.impl;

import my.spring.aop.proxy.demo.service.NoAspectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lee Tae Su
 * @version 1.0
 * @project spring-aop-demo
 * @since 2018-07-24
 */
@Service
public class NoAspectServiceImpl implements NoAspectService {
	@Transactional
	@Override
	public void noDemo() {
		System.out.println("NoAspectServiceImpl noDemo() called");
	}
}
