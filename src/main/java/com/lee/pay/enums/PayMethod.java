package com.lee.pay.enums;

import com.lee.pay.alipay.AliPayConfig;
import com.lee.pay.ccbpay.CCBPayConfig;
import com.lee.pay.wxpay.entity.WXPayConfig;

public enum PayMethod {


    ALI_PAY("支付宝", 1, AliPayConfig.class),
    WX_PAY("微信", 2, WXPayConfig.class),
    CCB_PAY("建行",3, CCBPayConfig.class);

    public String value;
    public int code;
    public Class<?> configClass;

    PayMethod(String value, int code, Class<?> configClass) {
        this.value = value;
        this.code = code;
        this.configClass = configClass;
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }

    public Class<?> getClazz() {
        return  configClass;
    }
}
