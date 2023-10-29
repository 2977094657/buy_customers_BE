package com.example.explor_gastro.common.utils;

import lombok.Data;

@Data
public class Response<T> {
    private Integer code;
    private String msg;
    private T data;
}
