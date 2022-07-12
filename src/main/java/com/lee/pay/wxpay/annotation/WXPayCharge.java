package com.lee.pay.wxpay.annotation;




import com.lee.pay.wxpay.entity.WxPayType;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WXPayCharge {
    String payType() default WxPayType.NATIVE;
}
