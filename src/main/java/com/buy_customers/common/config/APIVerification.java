package com.buy_customers.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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
            CachedBodyHttpServletRequestWrapper wrappedRequest = new CachedBodyHttpServletRequestWrapper(request);
            if (preHandle(wrappedRequest, response)) {
                ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
                filterChain.doFilter(wrappedRequest, responseWrapper);
                postHandle(responseWrapper);
                responseWrapper.copyBodyToResponse();
            }
        } catch (Exception e) {
            System.out.println("校验异常: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":502, \"msg\":\"" + e.getMessage() + "\", \"data\":null}");
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

        // fixme 暂时放弃，频繁读写redis服务器压力过大，考虑使用其他方法应对重放攻击
        // String nonceValue = stringRedisTemplate.opsForValue().get(nonce);
        // if (nonceValue == null) {
        //     returnErrorResponse(response, "无效或过期的 nonce");
        //     return false;
        // }

        long requestTime = Long.parseLong(timestamp);
        long currentTime = System.currentTimeMillis();
        if (currentTime - requestTime > 60000) {
            returnErrorResponse(response, "请求已过期");
            return false;
        }

        Map<String, String[]> parameterMap = getParameterMap(request);

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
            // System.out.println("后端计算的sign："+generatedSign);
            // System.out.println("前端的sign："+sign);
            return false;
        }

        return true;
    }

    private Map<String, String[]> getParameterMap(HttpServletRequest request) throws IOException {
        Map<String, String[]> parameterMap = new HashMap<>();

        // 检查是否是Multipart请求
        if (isMultipartContent(request)) {
            MultipartHttpServletRequest multipartRequest = getMultipartRequest(request);
            if (multipartRequest != null) {
                for (Iterator<String> it = multipartRequest.getFileNames(); it.hasNext();) {
                    String key = it.next();
                    MultipartFile file = multipartRequest.getFile(key);
                    if (file != null) {
                        // 可以根据需要处理文件
                        System.out.println("File: " + file.getOriginalFilename());
                    }
                }
                parameterMap.putAll(multipartRequest.getParameterMap());
            }
        } else if ("PUT".equalsIgnoreCase(request.getMethod()) || "POST".equalsIgnoreCase(request.getMethod()) || "DELETE".equalsIgnoreCase(request.getMethod())) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String requestBody = sb.toString();

            if (!requestBody.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> bodyParams = mapper.readValue(requestBody, Map.class);
                flattenParams("", bodyParams, parameterMap);
            }
        }

        if (parameterMap.isEmpty()) {
            parameterMap = request.getParameterMap();
        }

        return parameterMap;
    }

    private boolean isMultipartContent(HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart/");
    }

    private MultipartHttpServletRequest getMultipartRequest(HttpServletRequest request) {
        if (request instanceof MultipartHttpServletRequest) {
            return (MultipartHttpServletRequest) request;
        } else if (request instanceof HttpServletRequestWrapper) {
            return getMultipartRequest((HttpServletRequest) ((HttpServletRequestWrapper) request).getRequest());
        } else {
            return null;
        }
    }

    private void flattenParams(String prefix, Map<String, Object> map, Map<String, String[]> result) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                flattenParams(key, (Map<String, Object>) entry.getValue(), result);
            } else if (entry.getValue() instanceof Collection) {
                result.put(key, ((Collection<?>) entry.getValue()).toArray(new String[0]));
            } else {
                result.put(key, new String[]{entry.getValue().toString()});
            }
        }
    }



    private void postHandle(ContentCachingResponseWrapper response) throws IOException, NoSuchAlgorithmException {
        byte[] content = response.getContentAsByteArray();
        String responseBody = new String(content, response.getCharacterEncoding());

        // 记录要调试的响应正文
        // System.out.println("响应正文: " + responseBody);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(responseBody.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            hexString.append(String.format("%02x", b));
        }
        String responseSign = hexString.toString();

        // 将计算出的响应符号记录到调试中
        // System.out.println("计算响应符号: " + responseSign);

        response.setHeader("X-Response-Sign", responseSign);
        response.copyBodyToResponse(); // 确保响应正文被正确写回
    }

    private void returnErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"code\":400, \"msg\":\"" + message + "\", \"data\":null}");
    }
}