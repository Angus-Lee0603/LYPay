package com.lee.pay.alipay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.lee.pay.aop.BasePayAopImpl;
import com.lee.pay.entity.AbstractOrderType;
import com.lee.pay.enums.PayMethod;
import com.lee.pay.exception.MyPaymentException;
import com.lee.pay.utils.crud.mapper.RawSqlMapper;
import com.lee.pay.utils.redis.PayRedisUtil;
import com.lee.pay.utils.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 */
@Slf4j
@Aspect
@Component
public class AliPayAop extends BasePayAopImpl {
    /**
     * 应用ID
     */
    private String appId;
    // 商户私钥，您的PKCS8格式RSA2私钥
    private String rsa2PrivateKey;
    // 查看地址：https://openhome.alipay.com/platform/keyManage.html 对应APPID下的支付宝公钥。
    private String aliPayPublicKey;
    // 服务器异步通知页面路径
    // 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    private String notifyUrl;
    // 签名方式
    private final String signType = "RSA2";
    // 字符编码格式
    private final String charset = "utf-8";

    public AliPayAop(WebSocketService websocketService, PayRedisUtil payRedisUtil, RawSqlMapper rawSqlMapper) {
        super(websocketService, payRedisUtil, rawSqlMapper);
    }


    @Pointcut("@annotation(com.lee.pay.alipay.annotation.AliPayCharge)")
    public void pay() {
    }

    @Pointcut("@annotation(com.lee.pay.alipay.annotation.AliPayCallBack)")
    public void callback() {
    }

    @Pointcut("@annotation(com.lee.pay.alipay.annotation.AliPayRefund)")
    public void refund() {
    }


    @Around(value = "pay()")
    public Object aroundPay(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();

        Object[] args = joinPoint.getArgs();
        AbstractOrderType orderType = (AbstractOrderType) args[4];
        String returnUrl = (String) args[3];
        String subject = (String) args[2];
        String totalAmount = (String) args[1];
        String outTradeNo = (String) args[0];


        //设置或更新商家账户参数
        setOrUpdate(orderType);

        //获得初始化的AlipayClient
        // 支付宝网关
        String GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, appId, rsa2PrivateKey, "json", charset,
                aliPayPublicKey, signType);

        //创建aliPay的request
        AlipayTradePagePayRequest aliPayRequest = new AlipayTradePagePayRequest();


        //封装参数
        HashMap<String, Object> params = new HashMap<>();
        params.put("out_trade_no", outTradeNo);
        params.put("total_amount", totalAmount);
        params.put("subject", subject);
        params.put("product_code", "FAST_INSTANT_TRADE_PAY");

        String content = JSON.toJSONString(params);

        aliPayRequest.setBizContent(content);

        //设置同步回调地址
        aliPayRequest.setReturnUrl(returnUrl);
        //设置异步回调地址
        aliPayRequest.setNotifyUrl(notifyUrl);
        //生成表单
        String form = null;
        try {
            form = alipayClient.pageExecute(aliPayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        HashMap<String, String> res = new HashMap<>();
        res.put("out_trade_no", outTradeNo);
        res.put("form", form);
        return res;

    }

    @Around(value = "callback()")
    public Object aroundCallback(ProceedingJoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();

        Map<String, String> params = new HashMap<>();

        HttpServletRequest request = getRequest();

        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            /*乱码解决，这段代码在出现乱码时使用
          valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
*/
            params.put(name, valueStr);
        }
        //验签
        boolean signVerified;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, aliPayPublicKey, charset, signType);
        } catch (AlipayApiException e) {
            throw new RuntimeException("验签异常" + e.getErrMsg());
        }


	/* 实际验证过程建议商户务必添加以下校验：
	1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
	2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
	3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
	4、验证app_id是否为该商户本身。
	*/
        if (signVerified) {
            //验证成功
            //商户订单号
            String outTradeNo = params.get("out_trade_no");
            //交易状态
            String trade_status = params.get("trade_status");

            if ("TRADE_FINISHED".equals(trade_status)) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                System.out.println("暂时不做处理");

                //注意：
                //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
            } else if ("TRADE_SUCCESS".equals(trade_status)) {
                JSONObject res = new JSONObject();
                res.put("status", true);
                res.put("payMethod", PayMethod.ALI_PAY.value);
                //支付宝有自动跳转，好像只要通知管理后台
                wsSendMessage(getUser(outTradeNo), res.toJSONString());

                try {
                    //业务处理
                    //构造参数
                    args[0] = params;
                    //切点方法执行
                    joinPoint.proceed(args);
                } catch (Throwable throwable) {
                    if (throwable.getClass() == MyPaymentException.class) {
                        log.error("单号为" + params.get("out_trade_no") + "的订单业务处理失败：", throwable.getMessage());
                    }
                    log.error("单号为" + params.get("out_trade_no") + "的订单业务处理异常：", throwable);
                }
                //注意：
                //付款完成后，支付宝系统发送该交易状态通知
                return "success";
            } else {
                JSONObject res = new JSONObject();
                res.put("status", false);
                res.put("payMethod", PayMethod.WX_PAY.value);
                wsSendMessage(getUser(outTradeNo), res.toJSONString());
                return "failed";
            }
        } else {//验证失败
            log.error("验签失败");
        }
        return "success";
    }

    @Around(value = "refund()")
    public Object aroundRefund(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();
        //获取入参
        Object[] args = joinPoint.getArgs();
        String outTradeNo = (String) args[0];
        double refundMoney = Double.parseDouble(getOrderMoney(outTradeNo));

        //获得初始化的AlipayClient
        // 支付宝网关
        String gatewayUrl = "https://openapi.alipay.com/gateway.do";
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl, appId, rsa2PrivateKey, "json", charset,
                aliPayPublicKey, signType);
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();


        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        bizContent.put("refund_amount", refundMoney);
        request.setBizContent(bizContent.toString());
        AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            log.info("支付宝退款调用成功");
            JSONObject res = new JSONObject();
            if ("REFUND_SUCCESS".equals(response.getRefundStatus())) {
                log.info("退款已经到账:" + refundMoney);
                res.put("refund_fee", response.getRefundAmount());
                res.put("out_trade_no", outTradeNo);
                res.put("code", 200);
            } else {
                res.put("code", 500);
            }
            return res;
        } else {
            throw new MyPaymentException("支付宝退款调用失败");
        }

    }


    private void setOrUpdate(AbstractOrderType orderType) {
        AliPayConfig config = getPayConfig(AliPayConfig.class);
        if (config == null) {
            throw new MyPaymentException("商家微信账户参数未配置");
        }
        this.appId = config.getAppId();
        this.rsa2PrivateKey = config.getRsa2PrivateKey();
        this.aliPayPublicKey = config.getAliPayPublicKey();
        this.notifyUrl = config.getNotifyUrl().get(orderType.name);

    }


}
