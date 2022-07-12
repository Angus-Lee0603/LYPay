package com.lee.pay.ccbpay.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CCBPayRefund {
    String version() default "V2";
}
