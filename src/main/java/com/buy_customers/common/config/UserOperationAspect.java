package com.buy_customers.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Aspect
@Component
public class UserOperationAspect {

    @Pointcut("execution(* com.buy_customers.controller.*.*(..))")
    public void userOperation() {
    }

    @Around("userOperation()")
    public Object logUserOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        // 获取用户操作的信息
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // 获取请求的信息
        String ip = request.getRemoteAddr();
        String url = request.getRequestURL().toString();
        String httpMethod = request.getMethod();  // 获取请求方法

        // 执行方法并获取响应的状态码和返回的数据内容
        Object result = joinPoint.proceed();
        int status = 0;
        if (response != null) {
            status = response.getStatus();
        }

        // 使用ObjectMapper将返回的对象转换为JSON字符串
        ObjectMapper objectMapper = new ObjectMapper();
        String resultJson = objectMapper.writeValueAsString(result);

        // 记录信息
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(new Date(System.currentTimeMillis()));
        System.out.println("Time: " + dateString);
        System.out.println("IP: " + ip);
        System.out.println("URL: " + url);
        System.out.println("HTTP Method: " + httpMethod);
        System.out.println("Method: " + methodName);
        System.out.println("Arguments: " + Arrays.toString(args));
        System.out.println("Response status: " + status);
        System.out.println("Response content: " + resultJson);

        return result;
    }
}

