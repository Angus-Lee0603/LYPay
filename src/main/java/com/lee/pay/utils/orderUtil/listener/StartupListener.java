package com.lee.pay.utils.orderUtil.listener;

import com.lee.pay.utils.orderUtil.OrderTypeImporter;
import com.lee.pay.utils.orderUtil.ThreadPoolUtils;
import com.lee.pay.utils.orderUtil.service.DelayService;
import com.lee.pay.utils.orderUtil.service.OrderRedisService;
import com.lee.pay.utils.orderUtil.entity.DshOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Set;

@Slf4j
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private final DelayService delayService;
    private final OrderRedisService redisService;

    public StartupListener(DelayService delayService, OrderRedisService redisService) {
        this.delayService = delayService;
        this.redisService = redisService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent evt) {
        log.info("系统启动完成 ==> {}", evt.getSource());
        setOrderType();
        log.info("注入交易订单类型 ==> {}", OrderTypeImporter.getMap());
        //自动取消订单
        delayService.start(new DelayService.OnDelayedListener() {
            @Override
            public void onDelayedArrived(final DshOrder order) {
                //异步来做
                ThreadPoolUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        String itrOrderId = order.getOrderId();
                        //查库判断是否需要自动取消订单
                        int surpsTime = redisService.getSurplusTime(itrOrderId).intValue();
                        log.info("redis键: " + itrOrderId + " 剩余过期时间:" + surpsTime);
                        if (surpsTime > 0) {
                            log.info("没有需要取消的订单!");
                        } else {
                            log.info("自动取消订单，删除队列:" + itrOrderId);
                            //从队列中删除
                            delayService.remove(itrOrderId);
                            //从redis删除
                            redisService.deleteOrder(itrOrderId);
                            //对订单进行取消订单操作 修改订单状态
                            String orderId = itrOrderId.substring(itrOrderId.lastIndexOf(":") + 1);
                            orderWithdrawAction(orderId);

                        }
                    }
                });
            }
        });
        //查找需要入队的订单
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                log.info("查找需要入队的订单");
                Set<String> keys = redisService.scan();
                if (keys == null || keys.size() <= 0) {
                    return;
                }
                log.info("需要入队的订单keys：" + keys);
                for (String key : keys) {
                    ThreadPoolUtils.execute(new Runnable() {
                        @Override
                        public void run() {
                            String orderKey = redisService.getOrder(key);
                            int surpsTime = redisService.getSurplusTime(key).intValue();
                            log.info("redis键:" + key + ";剩余过期时间:" + surpsTime);
                            if (orderKey != null) {
                                DshOrder dshOrder = new DshOrder(orderKey, surpsTime);
                                delayService.add(dshOrder);
                                log.info("订单自动入队：" + dshOrder);
                            }
                        }
                    });

                }
            }
        });
    }

    protected void orderWithdrawAction(String orderId) {
    }

    protected void setOrderType() {
    }
}
 