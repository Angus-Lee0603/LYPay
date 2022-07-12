package com.lee.project;

import com.lee.pay.alipay.annotation.AliPayCallBack;
import com.lee.pay.alipay.annotation.AliPayCharge;
import com.lee.pay.entity.AbstractOrderType;
import com.lee.pay.enums.OrderFrom;
import com.lee.pay.enums.PayMethod;
import com.lee.pay.utils.orderUtil.OrderUtil;
import com.lee.pay.wxpay.annotation.WXPayCallback;
import com.lee.pay.wxpay.annotation.WXPayCharge;
import com.lee.pay.wxpay.entity.WxPayType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MeetingOrderService extends AbstractOrderPayService<MeetingOrderService> {


    public MeetingOrderService(OrderUtil orderUtil) {
        super(orderUtil);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, String> payOrderAction(PayMethod method, Integer orderFrom, String outTradeNo, String orderSubject, String totalAmount,
                                                 String userId, String redirectUrl, OrderType orderType) {

        switch (method) {
            case WX_PAY:
                if (orderFrom == 1)
                    return (Map<String, String>) getService().wxPayCharge2(outTradeNo, totalAmount, orderSubject, orderType, userId);
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
        return null;
    }


    @AliPayCharge
    public Object aliPayCharge(String outTradeNo, String totalAmount, String orderSubject, String redirectUrl,
                               AbstractOrderType abstractOrderType) {
        return null;
    }

    @AliPayCallBack
    public Object aliPayCallBack(Map<String, String> params) {
        return null;
    }


}
