package my.spring.aop.proxy.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author Lee Tae Su
 * @version 1.0
 * @project spring-aop-demo
 * @since 2018-07-24
 */
@Entity
@Table
@Getter
@NoArgsConstructor
public class ProxyDemo {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEMO_SEQ")
	@SequenceGenerator(sequenceName = "DEMO_SEQ", name = "DEMO_SEQ")
	private Long key;
}
