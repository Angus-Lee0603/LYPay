package com.lee.pay.alipay.annotation;

import java.lang.annotation.*;

/**
 * @author Administrator
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AliPayCallBack {}
