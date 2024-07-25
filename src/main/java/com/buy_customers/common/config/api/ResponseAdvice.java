package com.buy_customers.common.config.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;

@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(@NotNull MethodParameter methodParameter, @NotNull Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object o, @NotNull MethodParameter methodParameter, @NotNull MediaType mediaType, @NotNull Class<? extends HttpMessageConverter<?>> aClass, @NotNull ServerHttpRequest serverHttpRequest, @NotNull ServerHttpResponse serverHttpResponse) {
        // System.out.println("返回对象类型: " + o.getClass().getName());
        if(o instanceof String){
            return objectMapper.writeValueAsString(ResultData.success(o));
        }
        //如果返回的结果是对象，即已经封装好的，直接返回即可。
        if (o instanceof ResultData) {
            return o;
        }
        // myBatisPlus的分页对象
        if (o instanceof IPage){
            return o;
        }
        // 加密后的数据格式
        if (o instanceof ObjectNode) {
            return o;
        }
        return ResultData.success(o);
    }
}
