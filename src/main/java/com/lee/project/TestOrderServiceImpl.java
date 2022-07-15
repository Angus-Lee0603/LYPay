package com.lee.project;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.pay.alipay.annotation.AliPayCallBack;
import com.lee.pay.alipay.annotation.AliPayCharge;
import com.lee.pay.entity.AbstractOrderType;
import com.lee.pay.enums.PayMethod;
import com.lee.pay.utils.orderUtil.OrderUtil;
import com.lee.pay.wxpay.annotation.WXPayCallback;
import com.lee.pay.wxpay.annotation.WXPayCharge;
import com.lee.pay.wxpay.entity.WxPayType;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class TestOrderServiceImpl extends ServiceImpl<TestOrderMapper, TestOrder> implements ITestOrderService {

    @Getter
    private final TestOrderPayServiceImpl payService;

    public TestOrderServiceImpl(@Lazy TestOrderPayServiceImpl payService) {
        this.payService = payService;
    }


    @Component
    class TestOrderPayServiceImpl extends AbstractOrderPayService<TestOrderPayServiceImpl> {

        private final OrderUtil orderUtil;

        public TestOrderPayServiceImpl(OrderUtil orderUtil) {
            super(orderUtil);
            this.orderUtil = orderUtil;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Map<String, String> payOrderAction(PayMethod method, Integer orderFrom, String outTradeNo,
                                                     String orderSubject, String totalAmount,
                                                     String userId, String redirectUrl, OrderType orderType) {

            //创建订单
            TestOrder order = TestOrder.builder()
                    .orderId(outTradeNo)
                    .payMethod(method.value)
                    .realAmount(new BigDecimal(totalAmount))
                    .userId(userId).build();
            //保存订单
            TestOrderServiceImpl.this.save(order);

            //构建 itrOrderId
            String itrOrderId = "order:" + OrderType.forValue(orderType.code).name + ":" + outTradeNo;

            //允许挂单，几分钟内未付费则取消
            orderUtil.pendingOrderToDelay(itrOrderId, order, orderType.pendingTime);

            switch (method) {
                case WX_PAY:
                    if (orderFrom == 1) {
                        return (Map<String, String>) getService().wxPayCharge2(outTradeNo, totalAmount, orderSubject, orderType, userId);
                    }
                    return (Map<String, String>) getService().wxPayCharge1(outTradeNo, totalAmount, orderSubject, orderType);
                case ALI_PAY:
                    return (Map<String, String>) getService().aliPayCharge(outTradeNo, totalAmount, orderSubject, redirectUrl, orderType);

            }
            return null;
        }


        @WXPayCharge
        public Object wxPayCharge1(String outTradeNo, String totalAmount, String orderSubject,
                                   AbstractOrderType abstractOrderType) {
            return null;
        }

        @WXPayCharge(payType = WxPayType.JSAPI)
        public Object wxPayCharge2(String outTradeNo, String totalAmount, String orderSubject,
                                   AbstractOrderType abstractOrderType, String openId) {
            return null;
        }

        @WXPayCallback
        public Object wxPayCallBack(Map<String, String> params) {
            return orderPayCallbackAction(params);
        }


        @AliPayCharge
        public Object aliPayCharge(String outTradeNo, String totalAmount, String orderSubject, String redirectUrl,
                                   AbstractOrderType abstractOrderType) {
            return null;
        }

        @AliPayCallBack
        public Object aliPayCallBack(Map<String, String> params) {
            return orderPayCallbackAction(params);
        }


        public Object orderPayCallbackAction(Map<String, String> params) {
            //此时已成功回调
            String orderTradeNo = params.get("out_trade_no");
            //订单状态改为已支付
            TestOrderServiceImpl.this.lambdaUpdate().set(TestOrder::getState, 1).eq(TestOrder::getOrderId, orderTradeNo).update();
            //从延迟队列中移除对应订单
            String delOrderId = "order:" + OrderType.TESTORDER.name + ":" + orderTradeNo;
            orderUtil.removeOrderFromDelay(delOrderId);

            return true;
        }

    }


    public Map<String, String> invokeOrderPay(BasePayParams payParams) {
        return payService.invokePayOrder(payParams);
    }


}
