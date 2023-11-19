package com.buy_customers;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@SpringBootApplication
@Slf4j
@MapperScan("com.buy_customers.dao")
public class BuyCustomersApplication extends WebMvcConfigurationSupport{

    public static void main(String[] args) {
        SpringApplication.run(BuyCustomersApplication.class, args);
        log.info("http://localhost:8081/doc.html");
    }
    @Override

    protected void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
