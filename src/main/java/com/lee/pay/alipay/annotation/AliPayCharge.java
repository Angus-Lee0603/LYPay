package com.lee.pay.alipay.annotation;

import java.lang.annotation.*;

/**
 * @author lee
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AliPayCharge {
}
