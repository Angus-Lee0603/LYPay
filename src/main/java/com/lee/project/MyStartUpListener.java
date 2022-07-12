package com.lee.project;


import com.lee.pay.entity.AbstractOrderType;
import com.lee.pay.utils.orderUtil.OrderTypeImporter;
import com.lee.pay.utils.orderUtil.listener.StartupListener;
import com.lee.pay.utils.orderUtil.service.DelayService;
import com.lee.pay.utils.orderUtil.service.OrderRedisService;
import org.springframework.stereotype.Component;

@Component
public class MyStartUpListener extends StartupListener {
    public MyStartUpListener(DelayService delayService, OrderRedisService redisService) {
        super(delayService, redisService);
    }

    /**
     * 订单取消动作 用于定时取消状态
     *
     * @param orderId 订单号
     */
    @Override
    protected void orderWithdrawAction(String orderId) {
        //todo：操作
        Integer type = Integer.parseInt(orderId.substring(orderId.length() - 1));
        AbstractOrderType orderType = OrderType.forValue(type);

    }


    /**
     * 注入自定义交易订单类型
     */
    @Override
    protected void setOrderType() {
        //必做
        OrderTypeImporter.setMap(OrderType.typesByValue);
    }
}
