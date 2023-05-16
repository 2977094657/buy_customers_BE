package com.example.explor_gastro.utils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        // 从请求头中获取 Authorization 字段
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 检查 Authorization 字段是否以 "Bearer " 开头
        if (StringUtils.isNotBlank(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            // 提取 JWT
            String jwt = authorizationHeader.substring(7);
            // 验证 JWT
            if (jwtService.validateToken(jwt)) {
                // 如果 JWT 验证通过，将 Authentication 对象设置到 SecurityContextHolder 中
                Authentication authentication = jwtService.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}
