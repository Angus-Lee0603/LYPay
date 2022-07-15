package com.lee.project;


import com.lee.pay.utils.crud.mapper.RawSqlMapper;
import com.lee.pay.utils.orderUtil.OrderTypeImporter;
import com.lee.pay.utils.orderUtil.listener.StartupListener;
import com.lee.pay.utils.orderUtil.service.DelayService;
import com.lee.pay.utils.orderUtil.service.OrderRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyStartUpListener extends StartupListener {

    private final RawSqlMapper mapper;

    public MyStartUpListener(DelayService delayService, OrderRedisService redisService, RawSqlMapper mapper) {
        super(delayService, redisService);
        this.mapper = mapper;
    }

    /**
     * 注入自定义交易订单类型
     */
    @Override
    protected void setOrderType() {
        //必做
        OrderTypeImporter.setMap(OrderType.typesByValue);
    }

    /**
     * 订单取消动作 用于定时取消状态
     *
     * @param orderId 订单号
     */
    @Override
    protected void orderWithdrawAction(String orderId) {

        Integer type = Integer.parseInt(orderId.substring(orderId.length() - 1));
        OrderType orderType = OrderType.typesByValue.get(type);
        String tableName = orderType.tableName;

        //设置取消 code（可自定义）
        int cancelCode = -1;
        //取消订单
        mapper.rawUpdate(
                "update " + tableName + " set state=" + cancelCode + " where order_id = '" + orderId + "'"
        );
    }
}
