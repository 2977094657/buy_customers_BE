package com.buy_customers.common.utils;

import lombok.Data;

@Data
public class Response<T> {
    private Integer code;
    private String msg;
    private T data;

//     Response<String> response = new Response<>();
//                 response.setCode(400);
//                 response.setMsg("用户名或手机号不存在或密码错误");
//                 return new ResponseEntity<>(response, HttpStatus.OK);
}
