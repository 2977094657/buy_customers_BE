package com.buy_customers.common.config.api;

import lombok.Getter;

@Getter
public enum ReturnCode {
    RC200(200,"请求成功"),
    RC400(400,"请求失败"),
    RC403(403,"无访问权限,请联系管理员授予权限"),
    RC500(500,"系统异常，请稍后重试");

    /**自定义状态码**/
    private final Integer code;
    /**自定义描述**/
    private final String msg;

    ReturnCode(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
}