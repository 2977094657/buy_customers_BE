package com.buy_customers;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@SpringBootApplication
@Slf4j
@MapperScan("com.buy_customers.dao")
public class BuyCustomersApplication extends WebMvcConfigurationSupport{

    public static void main(String[] args) {
        SpringApplication.run(BuyCustomersApplication.class, args);
    }
}
