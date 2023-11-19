package com.buy_customers.common.utils;

import lombok.Data;

@Data
public class Response<T> {
    private Integer code;
    private String msg;
    private T data;
}
