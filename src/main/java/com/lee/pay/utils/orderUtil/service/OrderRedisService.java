package com.lee.pay.utils.orderUtil.service;


import com.lee.pay.entity.BaseOrder;

import java.util.Set;

public interface OrderRedisService {

    /**
     * 订单对象加入缓存
     *
     * @param orderId     订单id
     * @param orderObject 订单对象
     * @param time        过期时间（s）
     */
    void saveOrder(String orderId, BaseOrder orderObject, Integer time);

    /**
     * 获得缓存订单对象
     *
     * @param orderId 订单id
     * @return
     */
    String getOrder(String orderId);

    /**
     * 删除缓存订单对象
     *
     * @param orderId 订单id
     */
    void deleteOrder(String orderId);

    /**
     * 查询所有需要缓存的订单对象
     *
     * @return
     */
    Set<String> scan();

    /**
     * 获得redis键的剩余时间
     *
     * @param key redis键
     * @return 剩余时间
     */
    Long getSurplusTime(String key);

}
 