package com.lee.pay.ccbpay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lee.pay.ccbpay.annotation.CCBPayRefund;
import com.lee.pay.aop.BasePayAopImpl;
import com.lee.pay.ccbpay.entity.PayCallBackEntity;
import com.lee.pay.ccbpay.entity.QrURL;
import com.lee.pay.ccbpay.entity.RSASig;
import com.lee.pay.enums.PayMethod;
import com.lee.pay.exception.MyPaymentException;
import com.lee.pay.utils.*;
import com.lee.pay.utils.crud.mapper.RawSqlMapper;
import com.lee.pay.utils.redis.PayRedisUtil;
import com.lee.pay.utils.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Slf4j
@Component
public class CCBPayAop extends BasePayAopImpl {

    private String merchantId;

    private String posId;

    private String branchId;

    private String pubKey30;

    private String pubKey;

    private String operator;

    private String operatorPassword;

    private String ebsIp;

    private Integer ebsPort;

    private String certFilePath;

    private String certFilePassword;

    private String configFilePath;

    public CCBPayAop(WebSocketService websocketService, PayRedisUtil payRedisUtil, RawSqlMapper rawSqlMapper) {
        super(websocketService, payRedisUtil, rawSqlMapper);
    }


    @Pointcut("@annotation(com.lee.pay.ccbpay.annotation.CCBPayCharge)")
    public void pay() {
    }

    @Pointcut("@annotation(com.lee.pay.ccbpay.annotation.CCBPayRefund)")
    public void refund() {
    }

    @Pointcut("@annotation(com.lee.pay.ccbpay.annotation.CCBPayCallback)")
    public void callback() {
    }

    @Around(value = "pay()")
    public Object aroundPay(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();

        Object[] args = joinPoint.getArgs();
        String outTradeNo = (String) args[0];
        String totalAmount = (String) args[1];
        String subject = (String) args[2];

        setOrUpdate();

        //以下都是固定死的参数，所以用定义成局部变量
        String CURCODE = "01";
        String TXCODE = "530550";
        String REMARK1 = "";
        String REMARK2 = "";
        String RETURNTYPE = "3";
        String TIMEOUT = "";

        Map<String, Object> map = new HashMap<>();
        map.put("CCB_IBSVersion", "V6");    //必输项
        map.put("MERCHANTID", merchantId);
        map.put("BRANCHID", branchId);
        map.put("POSID", posId);
        map.put("ORDERID", outTradeNo);
        map.put("PAYMENT", totalAmount);
        map.put("CURCODE", CURCODE);
        map.put("TXCODE", TXCODE);
        map.put("REMARK1", REMARK1);
        map.put("REMARK2", REMARK2);
        map.put("RETURNTYPE", RETURNTYPE);
        map.put("TIMEOUT", TIMEOUT);
        String tmp = "MERCHANTID=" + merchantId + "&posId=" + posId + "&BRANCHID=" + branchId + "&ORDERID=" + outTradeNo +
                "&PAYMENT=" + totalAmount + "&CURCODE=" + CURCODE + "&TXCODE=" + TXCODE + "&REMARK1=" + REMARK1 +
                "&REMARK2=" + REMARK2 + "&RETURNTYPE=" + RETURNTYPE + "&TIMEOUT=" + TIMEOUT + "&PUB=" + pubKey30;//验签字段
        map.put("MAC", MD5Util.md5Str(tmp));
        map.put("PROINFO", URLCoder.escape(subject));//商品信息，中文要用自己写的escape方法进行编码，不然会乱码（此字段不参加验证）
        System.out.println(map);

        String bankURL = "https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain?CCB_IBSVersion=V6";
        String ret = HttpClientUtil.doPost(bankURL, map);    //请求二维码生成链接串
        System.out.println("ret::" + ret);
        QrURL qrURL = JSON.parseObject(ret, QrURL.class);
        //获取二维码串
        ret = HttpClientUtil.doGet(qrURL.getPAYURL(), "UTF-8");

        QrURL qr = JSON.parseObject(ret, QrURL.class);
        String decodeUrl = "";
        try {
            decodeUrl = URLDecoder.decode(qr.getQRURL(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, String> res = new HashMap<>();
        res.put("out_trade_no", outTradeNo);
        res.put("url", decodeUrl);
        return res;
    }


    @Around(value = "callback()")
    public Object aroundCallback(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        PayCallBackEntity payCallBackEntity = (PayCallBackEntity) args[0];

        RSASig rsaSig = new RSASig();
        // 验签
        rsaSig.setPublicKey(pubKey);
        String src =
                "POSID=" + payCallBackEntity.getPOSID() + "&BRANCHID=" + payCallBackEntity.getBRANCHID() + "&ORDERID=" + payCallBackEntity.getORDERID()
                        + "&PAYMENT=" + payCallBackEntity.getPAYMENT() + "&CURCODE=" + payCallBackEntity.getCURCODE() + "&REMARK1=" + payCallBackEntity.getREMARK1()
                        + "&REMARK2=" + payCallBackEntity.getREMARK2() + "&ACC_TYPE=" + payCallBackEntity.getACC_TYPE() + "&SUCCESS=" + payCallBackEntity.getSUCCESS();
        // 验签结果
        boolean signResult = rsaSig.verifySigature(payCallBackEntity.getSIGN(), src);
        if (!signResult) {
            log.error("验签失败！");
            return "SUCCESS";
        }
        String success = payCallBackEntity.getSUCCESS();
        JSONObject res = new JSONObject();
        if ("Y".equals(success)) {
            log.info("建行回调成功");
            //更改数据库订单状态
            res.put("status", true);
            res.put("payMethod", PayMethod.CCB_PAY.value);

            String user = getUser(payCallBackEntity.getORDERID());
            if (StringUtils.isNotBlank(user)) {
                log.info("发送ws消息");
                wsSendMessage(user, res.toJSONString());
            }
            // 支付成功
            //进行业务处理
            try {
                //为了统一这里做一步处理
                joinPoint.proceed(args);
            } catch (Throwable throwable) {
                if (throwable.getClass() == MyPaymentException.class) {
                    log.error("单号为" + payCallBackEntity.getORDERID() + "的订单业务处理失败：", throwable.getMessage());
                }
                log.error("单号为" + payCallBackEntity.getORDERID() + "的订单业务处理异常：", throwable);

            }
        } else {
            res.put("status", false);
            res.put("payMethod", PayMethod.CCB_PAY.value);

            wsSendMessage(getUser(payCallBackEntity.getORDERID()), res.toJSONString());
        }
        // 不论支付成功失败，给银行一个返回结果
        return "SUCCESS";

    }

    @Around(value = "refund()")
    public Object aroundRefund(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();
        //获取入参
        Object[] args = joinPoint.getArgs();
        String outTradeNo = (String) args[0];
        double refundMoney = Double.parseDouble(getOrderMoney(outTradeNo));


        //获取注解属性的值
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String version = signature.getMethod().getAnnotation(CCBPayRefund.class).version();
        String refund;
        JSONObject res = new JSONObject();
        if ("V1".equals(version)) {

            refund = EBSUtil.payByCcbRefund(operator, operatorPassword, refundMoney, merchantId, outTradeNo,
                    ebsIp, ebsPort);
        } else {
            refund = EBSUtilV2.payByCCBRefund(refundMoney, operator, operatorPassword, merchantId, outTradeNo,
                    certFilePath, certFilePassword, configFilePath);
        }
        res.put("refundOutTradeNo", outTradeNo);
        res.put("res", refund);
        return res;


    }


    private void setOrUpdate() {
        CCBPayConfig config = getPayConfig(CCBPayConfig.class);
        if (config == null) {
            throw new MyPaymentException("商家账户参数未配置");
        }
        this.merchantId = config.getMerchantId();
        this.posId = config.getPosId();
        this.branchId = config.getBranchId();
        this.pubKey = config.getPubKey();
        //公钥后30位
        this.pubKey30 = pubKey.substring(pubKey.length() - 30);
        this.operator = config.getOperator();
        //对密码进行解密赋值
        this.operatorPassword = AESUtil.aesDecode(config.getOperatorPassword());
        String ebsHost = config.getEbsHost();
        if (StringUtils.isNotBlank(ebsHost)) {
            this.ebsIp = ebsHost.substring(0, ebsHost.indexOf(":"));
            this.ebsPort = Integer.parseInt(ebsHost.substring(ebsHost.indexOf(":") + 1));
        }
        String certFilePath = config.getCertFilePath();
        String certFilePassword = config.getCertFilePassword();
        String configFilePath = config.getConfigFilePath();
        if (StringUtils.isNotBlank(certFilePath) && StringUtils.isNotBlank(certFilePassword)
                && StringUtils.isNotBlank(configFilePath)) {
            this.certFilePath = certFilePath;
            this.certFilePassword = AESUtil.aesDecode(certFilePassword);
            this.configFilePath = configFilePath;
        }

    }
}
