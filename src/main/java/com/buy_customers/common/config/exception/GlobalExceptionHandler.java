package com.buy_customers.common.config.exception;

import com.buy_customers.common.config.api.ResultData;
import com.buy_customers.common.config.api.ReturnCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.util.HashMap;
import java.util.Map;

import static cn.dev33.satoken.SaManager.log;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceededException() {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errorMessage", "请将图片控制在5MB内");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultData<String> exception(Exception e) {
        log.error("全局异常信息 {}", e.getMessage(), e);
        return ResultData.fail(ReturnCode.RC500.getCode(), e.getMessage());
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object o, @NotNull MethodParameter methodParameter, @NotNull MediaType mediaType, @NotNull Class<? extends HttpMessageConverter<?>> aClass, @NotNull ServerHttpRequest serverHttpRequest, @NotNull ServerHttpResponse serverHttpResponse) {
        if(o instanceof String){
            return objectMapper.writeValueAsString(ResultData.success(o));
        }
        if(o instanceof ResultData){
            return o;
        }
        return ResultData.success(o);
    }

    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
}