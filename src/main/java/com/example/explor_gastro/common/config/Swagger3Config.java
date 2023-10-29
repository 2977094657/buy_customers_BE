package com.example.explor_gastro.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

// 定义应用程序的OpenAPI规范
@OpenAPIDefinition(info = @Info(
        title = "explor_gastro API",
        version = "1.0.0",
        description = "探索美食是一款在线外卖平台，旨在提供方便快捷的美食订购服务，为用户带来无与伦比的用餐体验。平台将提供多种不同类型的餐厅和菜品，用户可以在平台上搜索、浏览、下单和支付，还可以跟踪订单状态和配送进度。"))

public class Swagger3Config {

    // 创建一个bean以对OpenAPI端点进行分组
    @Bean
    public GroupedOpenApi customOpenApi() {
        return GroupedOpenApi.builder()
                .group("my-api")
                .packagesToScan("com.example.myapp")
                .build();
    }
}
