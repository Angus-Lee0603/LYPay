package com.lee.pay.alipay;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class AliPayConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    // 应用ID
    private String appId;
    // 商户私钥，您的PKCS8格式RSA2私钥
    private String rsa2PrivateKey;
    // 查看地址：https://openhome.alipay.com/platform/keyManage.html 对应APPID下的支付宝公钥。
    private String aliPayPublicKey;
    // 服务器异步通知页面路径
    // 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    private Map<String, String> notifyUrl;

}
