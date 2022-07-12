package com.lee.pay.wxpay.entity;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class WXPayConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appId;

    private String appSecret;

    private String mchId;

    private String apiKey;

    private Map<String, String> notifyUrl;

    private String certStorePath;
}
