package my.spring.aop.proxy.demo.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Lee Tae Su
 * @version 1.0
 * @project spring-aop-demo
 * @since 2018-07-24
 */
public interface ProxyDemoRepository extends JpaRepository<ProxyDemo, Long> {
}
