package com.lee.pay.wxpay;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.lee.pay.wxpay.annotation.WXPayCharge;
import com.lee.pay.aop.BasePayAopImpl;
import com.lee.pay.entity.AbstractOrderType;
import com.lee.pay.enums.PayMethod;
import com.lee.pay.wxpay.entity.WxPayType;
import com.lee.pay.exception.MyPaymentException;
import com.lee.pay.utils.HttpClientUtil;
import com.lee.pay.utils.IPUtil;
import com.lee.pay.utils.crud.mapper.RawSqlMapper;
import com.lee.pay.utils.redis.PayRedisUtil;
import com.lee.pay.utils.websocket.service.WebSocketService;
import com.lee.pay.wxpay.entity.WXPayConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * @author liyi
 * 微信apiV2  因为没有https，如果有建议用apiV3感觉更方便
 */
@Slf4j
@Aspect
@Component
public class WXPayAop extends BasePayAopImpl {


    private String appId;

    private String mchId;

    private String apiKey;

    private String notifyUrl;

    private String certStorePath;


    public WXPayAop(WebSocketService websocketService, PayRedisUtil payRedisUtil, RawSqlMapper rawSqlMapper) {
        super(websocketService, payRedisUtil, rawSqlMapper);
    }

    @Pointcut("@annotation(com.lee.pay.wxpay.annotation.WXPayCharge)")
    public void pay() {
    }

    @Pointcut("@annotation(com.lee.pay.wxpay.annotation.WXPayCallback)")
    public void callback() {
    }

    @Pointcut("@annotation(com.lee.pay.wxpay.annotation.WXPayRefund)")
    public void refund() {
    }


    @Around(value = "pay()")
    public Object aroundPay(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();
        //获取入参
        Object[] args = joinPoint.getArgs();
        String outTradeNo = (String) args[0];
        String totalAmount = (String) args[1];
        String subject = (String) args[2];
        AbstractOrderType orderType = (AbstractOrderType) args[3];

        String openId = "";
        if (args.length == 5)
            openId = (String) args[4];

        //获取注解属性值
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String payType = signature.getMethod().getAnnotation(WXPayCharge.class).payType();

        //设置或更新商家账户参数
        setOrUpdate(orderType);
        //调起支付
        switch (payType) {
            case WxPayType.NATIVE:
                return payNative(subject, outTradeNo, totalAmount);
            case WxPayType.JSAPI:
                return payJsapi(subject, openId, outTradeNo, totalAmount);
            default:
                throw new IllegalStateException("Unexpected value: " + payType);
        }
    }

    /**
     * NATIVE 支付
     *
     * @param body        商品信息
     * @param outTradeNo  订单编号（内部）
     * @param totalAmount 支付金额
     * @return 返回参数
     */
    private Map<String, String> payNative(String body, String outTradeNo, String totalAmount) {
        Map<String, String> requestData = new HashMap<>();
        try {
            Map<String, String> data = new LinkedHashMap<>();
            data.put("appid", appId);//公众号appid或小程序appid
            data.put("attach", "no");
            data.put("body", body);//商品详情
            data.put("mch_id", mchId);//商户号
            data.put("nonce_str", WXPayUtil.generateNonceStr());//32位字符串
            data.put("notify_url", notifyUrl); //异步通知地址
            data.put("out_trade_no", outTradeNo);//订单号
            data.put("spbill_create_ip", IPUtil.getIpAddr(getRequest()));//用户终端IP
            data.put("total_fee", String.valueOf((int) (Double.parseDouble(totalAmount) * 100)));//总金额
            data.put("trade_type", WxPayType.NATIVE);
            //构造签名，调用生成签名的方法，用以Map集合中的相关参数生成签名 需要签名密钥
            String sign = WXPayUtil.generateSignature(data, apiKey);
            data.put("sign", sign);//签名
            String xml = WXPayUtil.generateSignedXml(data, apiKey);//转Xml格式 微信SDK自带的
            // 发送post请求   返回的是微信给我们的xml格式的数据
            String resultXML = HttpClientUtil.doPost(WXPayConstants.UNIFIEDORDER_URL, xml);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXML);

            //xml转map 微信SDK自带的
            String returnCode = resultMap.get("return_code");//状态码
            String resultCode = resultMap.get("result_code");//业务结果
            if (WXPayConstants.SUCCESS.equals(returnCode) && WXPayConstants.SUCCESS.equals(resultCode)) {
                requestData.put("outTradeNo", outTradeNo);
                requestData.put("codeUrl", resultMap.get("code_url"));
                requestData.put("package", "prepay_id=" + resultMap.get("prepay_id"));
                requestData.put("nonceStr", resultMap.get("nonce_str"));
            } else {
                throw new Exception(outTradeNo + "交易发起失败:" + resultMap.get("err_code_des"));
            }
        } catch (Exception e) {
            throw new MyPaymentException(e.getMessage());
        }
        return requestData;
    }

    /**
     * @param body        商品信息
     * @param openId      微信小程序用户id
     * @param outTradeNo  订单编号（内部）
     * @param totalAmount 支付金额
     * @return 返回参数
     */
    private Map<String, String> payJsapi(String body, String openId, String outTradeNo, String totalAmount) {
        Map<String, String> requestData = new HashMap<>();
        try {
            Map<String, String> data = new LinkedHashMap<>();
            data.put("appid", appId);//公众号appid或小程序appid
            data.put("attach", "no");
            data.put("body", body);//商品详情
            data.put("mch_id", mchId);//商户号
            data.put("nonce_str", WXPayUtil.generateNonceStr());//32位字符串
            data.put("notify_url", notifyUrl);//异步通知地址
            data.put("openid", openId);
            data.put("out_trade_no", outTradeNo);//订单号
            data.put("spbill_create_ip", IPUtil.getIpAddr(getRequest()));//用户终端IP
            data.put("total_fee", String.valueOf((int) (Double.parseDouble(totalAmount) * 100)));//总金额
            data.put("trade_type", WxPayType.JSAPI);
            //调用生成签名的方法，用以Map集合中的相关参数生成签名 需要签名密钥
            String sign = WXPayUtil.generateSignature(data, apiKey);
            data.put("sign", sign);//签名

            String xml = WXPayUtil.generateSignedXml(data, apiKey);//转Xml格式 微信SDK自带的
            String resultXML = HttpClientUtil.doPost(WXPayConstants.UNIFIEDORDER_URL, xml);//发送post请求   返回的是微信给我们的xml格式的数据

            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXML);
            //xml转map 微信SDK自带的
            String returnCode = resultMap.get("return_code");//状态码
            String resultCode = resultMap.get("result_code");//业务结果
            if (WXPayConstants.SUCCESS.equals(returnCode) && WXPayConstants.SUCCESS.equals(resultCode)) {
                requestData.put("outTradeNo", outTradeNo);
                requestData.put("package", "prepay_id=" + resultMap.get("prepay_id"));
                requestData.put("nonceStr", resultMap.get("nonce_str"));
                requestData.put("timeStamp", getGMTTimeStamp());
                requestData.put("paySign", getPaySign(requestData));
                requestData.put("appId", appId);
                requestData.put("signType", "MD5");
            } else {
                throw new Exception(outTradeNo + "交易发起失败:" + resultMap.get("err_code_des"));
            }

        } catch (Exception e) {
            throw new MyPaymentException(e.getMessage());
        }
        return requestData;
    }

    @Around(value = "callback()")
    public Object aroundCallback(ProceedingJoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();

        HttpServletRequest request = getRequest();
        HttpServletResponse response = getResponse();
        try {//解析回调的参数
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String result = new String(outSteam.toByteArray(), StandardCharsets.ISO_8859_1);
            Map<String, String> resMap = WXPayUtil.xmlToMap(result);
            // 判断签名是否正确 微信SDK自带的方法
            if (WXPayUtil.isSignatureValid(resMap, apiKey)) {
                log.info("微信支付成功回调");
                // ------------------------------
                // 处理业务开始
                // ------------------------------
                String resXml;
                JSONObject res = new JSONObject();
                String outTradeNo = resMap.get("out_trade_no");
                if ("SUCCESS".equals(resMap.get("result_code"))) {
                    res.put("status", true);
                    res.put("payMethod", PayMethod.WX_PAY.value);
                    String user = getUser(outTradeNo);
                    if (StringUtils.isNotBlank(user)) {
                        log.info("发送ws消息");
                        wsSendMessage(user, res.toJSONString());
                    }

                    // 支付成功,进行业务处理
                    try {
                        //为了统一这里做一步处理
                        resMap.put("totalAmount", String.valueOf(Double.parseDouble(resMap.get("total_fee")) * 0.01));
                        resMap.remove("total_fee");
                        args[0] = resMap;
                        joinPoint.proceed(args);
                    } catch (Throwable throwable) {
                        if (throwable.getClass() == MyPaymentException.class) {
                            log.error("单号为" + outTradeNo + "的订单业务处理失败：", throwable.getMessage());
                        }
                        log.error("单号为" + outTradeNo + "的订单业务处理异常：", throwable);
                    }
                    resXml = "<xml>"
                            + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>"
                            + "</xml> ";

                } else {
                    res.put("status", false);
                    res.put("payMethod", PayMethod.WX_PAY.value);
                    //websocket
                    String user = getUser(outTradeNo);
                    if (StringUtils.isNotBlank(user)) {
                        log.info("发送ws消息");
                        wsSendMessage(user, res.toJSONString());
                    }

                    log.info("支付失败,错误信息：{}", resMap.get("err_code"));
                    resXml = "<xml>" +
                            "<return_code><![CDATA[FAIL]]></return_code>"
                            + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                }
                // ------------------------------
                // 处理业务完毕
                // ------------------------------
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(resXml.getBytes());
                out.flush();
                out.close();
            } else {
                log.info("通知签名验证失败");
            }
        } catch (Exception e) {
            log.info("通知签名验证方法出现异常：");
            e.printStackTrace();
        }
        return 1;//返回什么不重要
    }

    @Around(value = "refund()")
    public Object aroundRefund(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();
        //获取入参
        Object[] args = joinPoint.getArgs();
        String outTradeNo = (String) args[0];
        String refundMoney = getOrderMoney(outTradeNo);

        Map<String, Object> requestData = new HashMap<>();

        //构建参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", appId); //公众账号ID
        paramMap.put("mch_id", mchId); //商户号
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        paramMap.put("out_refund_no", WXPayUtil.generateNonceStr());//商户退款单号
        paramMap.put("out_trade_no", outTradeNo);//商户订单号
        paramMap.put("total_fee", refundMoney);  //订单金额
        paramMap.put("refund_fee", refundMoney);  //退款金额
        String sign = WXPayUtil.generateSignature(paramMap, apiKey);
        paramMap.put("sign", sign); //签名
        String requestXmlString = WXPayUtil.mapToXml(paramMap);//转为xml字符串

        String resultXML = HttpClientUtil.doPost(WXPayConstants.REFUND_URL, requestXmlString, certStorePath, mchId);
        assert resultXML != null;

        Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXML);
        String returnCode = resultMap.get("return_code");//状态码
        String resultCode = resultMap.get("result_code");//业务结果
        if (WXPayConstants.FAIL.equals(returnCode) && !"订单已全额退款".equals(resultMap.get("err_code_des"))) {
            throw new MyPaymentException("退款失败：" + resultMap.get("err_code_des"));
        } else if ("FAIL".equals(returnCode)) {
            throw new MyPaymentException("退款失败：" + resultMap.get("return_msg"));
        }
        if (WXPayConstants.SUCCESS.equals(returnCode) && WXPayConstants.SUCCESS.equals(resultCode)) {
            double refund_fee = Double.parseDouble(resultMap.get("refund_fee"));
            log.info("退款已经到账:" + refundMoney);
            requestData.put("refundFee", refund_fee / 100);
            requestData.put("outTradeNo", outTradeNo);
            requestData.put("code", 200);
            return requestData;
        }
        throw new MyPaymentException("退款失败：" + resultMap.get("err_code_des"));
    }


    private String getPaySign(Map<String, String> map) throws Exception {

        Map<String, String> signMap = new HashMap<>();
        signMap.put("appId", appId);
        signMap.put("nonceStr", map.get("nonceStr"));
        signMap.put("package", map.get("package"));
        signMap.put("signType", "MD5");
        signMap.put("timeStamp", map.get("timeStamp"));
        return WXPayUtil.generateSignature(signMap, apiKey);
    }


    private void setOrUpdate(AbstractOrderType orderType) {
        WXPayConfig config = getPayConfig(WXPayConfig.class);
        if (config == null)
            throw new MyPaymentException("商家微信账户参数未配置");
        this.appId = config.getAppId();
        this.mchId = config.getMchId();
        this.apiKey = config.getApiKey();
        this.notifyUrl = config.getNotifyUrl().get(orderType.name);
        this.certStorePath = config.getCertStorePath();

    }

    /**
     * 北京时间 +8 小时
     *
     * @return 字符串型的时间戳
     */
    private String getGMTTimeStamp() {
        return String.valueOf((new Date().getTime() + 8 * 60 * 60 * 1000) / 1000);
    }

}
