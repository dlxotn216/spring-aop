package my.spring.aop.aspectj_weaber.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by taesu on 2018-08-21.
 */
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@EnableLoadTimeWeaving
public class AopAspectjWeaverDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(AopAspectjWeaverDemoApplication.class);
    }
}
