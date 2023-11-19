package com.buy_customers.common.config;

import com.buy_customers.common.utils.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.Resource;

/**
 * Spring Security 配置类
 * @author 46
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // 创建一个名为 securityFilterChain 的 Bean，这个 Bean 返回一个 SecurityFIlterChain 实例。
    // 在这个方法中，我们可以配置所有的安全规则。
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(corsFilter(), ChannelProcessingFilter.class) // 添加 CORS 过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf().disable() // 禁用 CSRF 防护
                .authorizeRequests()
//                .antMatchers("/**").hasRole("ADMIN")  //设置admin可以访问的接口路径
//                .antMatchers("/user/**").hasAnyRole("ADMIN", "USER")
//                .antMatchers("/**").permitAll()  // 所有人都可以访问定义的接口
//                .anyRequest().authenticated();  // 其他所有接口都需要验证过的用户才可以通过
                .anyRequest().permitAll();  // 允许所有用户通过
        return http.build();
    }

    // 创建一个名为 corsFilter 的 Bean，这个 Bean 返回一个 CorsFIlter 实例。
    // 在这个方法中，我们可以配置所有的跨域规则。
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");  // 允许任意来源
        config.addAllowedHeader("*");  // 允许任意请求头
        config.addAllowedMethod("*");  // 允许任意 HTTP 方法
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

