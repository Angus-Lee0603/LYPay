package com.lee.project;


import com.lee.pay.enums.PayMethod;
import com.lee.pay.exception.MyPaymentException;
import com.lee.pay.utils.orderUtil.OrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public abstract class AbstractOrderPayService<T> {
    //todo:注入订单service
    private final OrderUtil orderUtil;
    private final AllOrderService allOrderService;

    public AbstractOrderPayService(OrderUtil orderUtil, AllOrderService allOrderService) {
        this.orderUtil = orderUtil;
        this.allOrderService = allOrderService;
    }


    protected abstract Map<String, String> payOrderAction(PayMethod method, Integer orderFrom, String outTradeNo,
                                                          String orderSubject, String totalAmount,
                                                          String userId, String redirectUrl, OrderType orderType);


    public Map<String, String> invokePayOrder(BasePayParams payParams) throws MyPaymentException {
        PayMethod payMethod = payParams.getPayMethod();
        OrderType orderType = OrderType.typesByValue.get(payParams.getOrderType());
        Integer orderFrom = payParams.getOrderFrom();
        String redirect = payParams.getRedirect();
        String amount = payParams.getTotalAmount();
        String userId = payParams.getUserId();
        String userPhone = payParams.getUserPhone();
        String outTradeNo;
        String phone4 = userPhone.substring(userPhone.length() - 4);
        //创建系统订单：
        //创建系统订单号
        switch (payMethod) {
            case WX_PAY:
            case ALI_PAY:
                outTradeNo = orderUtil.generateOutTradeNo(phone4, orderType.code);
                break;
            case CCB_PAY:
                outTradeNo = orderUtil.generateCCBOutTradeNo(orderType.code);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + payMethod);
        }

        //创建相应订单
        String subject = "测试";
        if (StringUtils.isBlank(amount))
            amount = "商品表中商品价格";

        TestOrder order = TestOrder.builder()
                .orderId(outTradeNo)
                .payMethod(payMethod.value)
                .realAmount(new BigDecimal(amount))
                .userId(userId).build();
        allOrderService.getTestOrderService().save(order);

        //构建 itrOrderId
        String itrOrderId = "order:" + OrderType.forValue(orderType.code).name + ":" + outTradeNo;

        //允许挂单，几分钟内未付费则取消
        orderUtil.pendingOrderToDelay(itrOrderId, order, orderType.pendingTime);

        return payOrderAction(payMethod, orderFrom, outTradeNo, subject, amount, userId, redirect, orderType);
    }


    public Object OrderPayCallbackAction(Map<String, String> params) {
        //此时
        String orderTradeNo = params.get("out_trade_no");
        //订单状态改为已支付

        //从延迟队列中移除对应订单
        String delOrderId = "order:" + OrderType.TESTORDER.name + ":" + orderTradeNo;
        orderUtil.removeOrderFromDelay(delOrderId);
        return true;
    }

    @SuppressWarnings("unchecked")
    protected T getService() {
        return (T) AopContext.currentProxy();
    }


}
