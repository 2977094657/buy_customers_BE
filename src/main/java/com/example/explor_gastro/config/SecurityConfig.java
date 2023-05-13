package com.example.explor_gastro.config;

import com.example.explor_gastro.utils.JwtAuthenticationFilter;
import com.example.explor_gastro.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity // 启用 Spring Security
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtService jwtService;

    /**
     * 配置 HTTP 安全策略
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 禁用 CSRF 防护
                .authorizeRequests()
                .antMatchers("/user/loginIn","/user/register").permitAll() // /此处接口允许所有用户访问
//                .anyRequest().authenticated() // 其他接口需要经过身份验证才能访问
                .anyRequest().permitAll() // 允许所有用户通过
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class); // 添加 JwtAuthenticationFilter 过滤器
    }
}
