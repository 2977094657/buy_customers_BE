package com.buy_customers.common.config.api;

import lombok.Data;

@Data
public class ResultData<T> {
    /** 结果状态 ,具体状态码参见ResultData.java*/
    private Integer code;
    private String msg;
    private T data;
    private long timestamp ;


    public ResultData (){
        this.timestamp = System.currentTimeMillis();
    }


    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(ReturnCode.RC200.getCode());
        resultData.setMsg(ReturnCode.RC200.getMsg());
        resultData.setData(data);
        return resultData;
    }

    public static <T> ResultData<T> fail(Integer code, String msg) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(code);
        resultData.setMsg(msg);
        return resultData;
    }

}
