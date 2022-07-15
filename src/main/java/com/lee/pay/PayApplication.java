package com.lee.pay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {
        "com.lee"
})
@EnableAsync
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan(basePackages = {
        "com.lee.pay.config.mapper",
        "com.lee.pay.utils.crud.mapper",
        "com.lee.project"
})
public class PayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }

}
