package com.lee.pay.config.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.pay.ccbpay.CCBPayConfig;
import com.lee.pay.config.service.IPayConfigService;
import com.lee.pay.config.entity.PayConfig;
import com.lee.pay.config.mapper.PayConfigMapper;
import com.lee.pay.enums.PayMethod;
import com.lee.pay.utils.AESUtil;
import com.lee.pay.utils.redis.PayRedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


@Service
public class PayConfigServiceImpl extends ServiceImpl<PayConfigMapper, PayConfig> implements IPayConfigService {
    private final PayRedisUtil payRedisUtil;

    public PayConfigServiceImpl(PayRedisUtil payRedisUtil) {
        this.payRedisUtil = payRedisUtil;
    }

    @Override
    public Boolean setConfig(PayMethod method, PayConfig config) {
        String configType;
        Object o;
        switch (method) {
            //TODO：检查商家账户必填参数
            case WX_PAY:
            case ALI_PAY:
                configType = genConfigType(method.configClass);
                o = JSONObject.parseObject(JSON.toJSONString(config), method.configClass);
                break;
            case CCB_PAY:
                configType = genConfigType(method.configClass);
                CCBPayConfig ccbPayConfig = JSONObject.parseObject(JSON.toJSONString(config), CCBPayConfig.class);
                if (StringUtils.isNotBlank(ccbPayConfig.getCertFilePassword())) {
                    ccbPayConfig.setCertFilePassword(ccbPayConfig.getCertFilePassword());
                }
                ccbPayConfig.setOperatorPassword(AESUtil.AESEncode(config.getOperatorPassword()));
                o = ccbPayConfig;
                config.setOperatorPassword(AESUtil.AESEncode(config.getOperatorPassword()));
                config.setCertFilePassword(AESUtil.AESEncode(ccbPayConfig.getCertFilePassword()));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + method);

        }
        payRedisUtil.set("payConfig:" + configType, o);
        PayConfig dbConfig = lambdaQuery().eq(PayConfig::getConfigType, configType).one();
        if (dbConfig == null) {
            return save(config);
        }
        config.setId(dbConfig.getId());
        return updateById(config);


    }

    @Override
    public <E> E queryPayConfig(Class<E> payConfig) {
        String allClassName = payConfig.getName();
        String configType = allClassName.substring(allClassName.lastIndexOf('.') + 1);
        PayConfig dbConfig = lambdaQuery().eq(PayConfig::getConfigType, configType).one();
        return JSONObject.parseObject(JSON.toJSONString(dbConfig), payConfig);

    }

    private String genConfigType(Class<?> payConfig) {
        String className = payConfig.getName();
        return className.substring(className.lastIndexOf('.') + 1);
    }
}
