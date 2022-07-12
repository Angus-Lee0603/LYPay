package com.lee.pay.aop;

import com.lee.pay.entity.BaseOrder;

public interface IBasePayAop {

    String getUser(String outTradeNo);

    BaseOrder getOrder(String outTradeNo);

    String getOrderMoney(String outTradeNo);

    void wsSendMessage(String userId, String message);

    void wsBroadcast(String topic, String message);

    <E> E getPayConfig(Class<E> clazz);
}
