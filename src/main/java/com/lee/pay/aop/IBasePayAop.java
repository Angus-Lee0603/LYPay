package com.lee.pay.aop;

import com.lee.pay.entity.BaseOrder;

public interface IBasePayAop {

    String getUser(String outTradeNo);

    BaseOrder getOrder(String outTradeNo);

    String getOrderMoney(String outTradeNo);

    void wsSendMessage(String userId, String message);

    /**
     * ws 广播
     * @param topic 主题
     * @param message 消息
     */
    void wsBroadcast(String topic, String message);

    <E> E getPayConfig(Class<E> clazz);
}
