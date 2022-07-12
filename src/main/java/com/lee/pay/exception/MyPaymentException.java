package com.lee.pay.exception;

public class MyPaymentException extends RuntimeException {
    //异常信息
    private Integer code;

    //构造函数
    public MyPaymentException(String message) {
        super(message);
    }
    public MyPaymentException(String message, Integer code){
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
