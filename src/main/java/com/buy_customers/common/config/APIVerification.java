package com.buy_customers.common.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class APIVerification extends OncePerRequestFilter {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${sa-token.sign.secret-key}")
    private String secretKey;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws IOException, ServletException {
        // 检查请求头 X-Request-Type 是否为 setNonce
        String requestTypeHeader = request.getHeader("X-Request-Type");
        if ("setNonce".equalsIgnoreCase(requestTypeHeader)) {
            // 如果请求头存在且值为 setNonce，则放行
            filterChain.doFilter(request, response);
            return;
        }
        try {
            if (preHandle(request, response)) {
                ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
                filterChain.doFilter(request, responseWrapper);
                postHandle(responseWrapper);
                responseWrapper.copyBodyToResponse();
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":400, \"msg\":\"" + e.getMessage() + "\", \"data\":null}");
        }
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sign = request.getHeader("x-sign");
        String timestamp = request.getHeader("x-timestamp");
        String nonce = request.getHeader("x-nonce");

        if (sign == null || timestamp == null || nonce == null) {
            returnErrorResponse(response, "缺少校验参数");
            return false;
        }

        String nonceValue = stringRedisTemplate.opsForValue().get(nonce);
        if (nonceValue == null) {
            returnErrorResponse(response, "无效或过期的 nonce");
            return false;
        }

        long requestTime = Long.parseLong(timestamp);
        long currentTime = System.currentTimeMillis();
        if (currentTime - requestTime > 60000) {
            returnErrorResponse(response, "请求已过期");
            return false;
        }

        // 获取 URL 中的参数
        String queryString = request.getQueryString();
        Map<String, String[]> parameterMap = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            String[] params = queryString.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    parameterMap.put(keyValue[0], new String[]{keyValue[1]});
                }
            }
        }

        // 如果没有通过 getParameterMap 获取到参数，再尝试用 getParameterMap 获取
        if (parameterMap.isEmpty()) {
            parameterMap = request.getParameterMap();
        }

        if (parameterMap.isEmpty()) {
            return true;
        }

        String[] keys = parameterMap.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        StringBuilder paramsString = new StringBuilder();
        for (String key : keys) {
            paramsString.append(key).append('=').append(parameterMap.get(key)[0]).append('&');
        }
        paramsString.setLength(paramsString.length() - 1);

        // System.out.println("接收到的参数: " + paramsString); // 打印接收到的参数

        String signString = paramsString + "&timestamp=" + timestamp + "&nonce=" + nonce + "&secret=" + secretKey;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(signString.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            hexString.append(String.format("%02x", b));
        }
        String generatedSign = hexString.toString();

        if (!sign.equals(generatedSign)) {
            returnErrorResponse(response, "无效参数");
            return false;
        }

        return true;
    }


    private void postHandle(ContentCachingResponseWrapper response) throws IOException, NoSuchAlgorithmException {
        byte[] content = response.getContentAsByteArray();
        String responseBody = new String(content, response.getCharacterEncoding());

        // 记录要调试的响应正文
        System.out.println("响应正文: " + responseBody);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(responseBody.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            hexString.append(String.format("%02x", b));
        }
        String responseSign = hexString.toString();

        // 将计算出的响应符号记录到调试中
        System.out.println("计算响应符号: " + responseSign);

        response.setHeader("X-Response-Sign", responseSign);
        response.copyBodyToResponse(); // 确保响应正文被正确写回
    }

    private void returnErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"code\":400, \"msg\":\"" + message + "\", \"data\":null}");
    }
}