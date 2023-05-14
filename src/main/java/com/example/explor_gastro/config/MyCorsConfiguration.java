package com.example.explor_gastro.config;

import org.springframework.web.cors.CorsConfiguration;

public class MyCorsConfiguration {

    // 获取 CorsConfiguration 对象的静态方法
    public static CorsConfiguration getCorsConfiguration() {

        // 创建 CorsConfiguration 对象
        CorsConfiguration config = new CorsConfiguration();

        // 设置允许访问的域名
        config.addAllowedOrigin("*");

        // 设置允许的 HTTP 方法
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");

        // 设置允许的请求头
        config.addAllowedHeader("*");

        // 设置是否允许发送凭证（如 Cookie）
        config.setAllowCredentials(true);

        // 设置预检请求的缓存时间
        config.setMaxAge(3600L);

        // 返回 CorsConfiguration 对象
        return config;
    }
}