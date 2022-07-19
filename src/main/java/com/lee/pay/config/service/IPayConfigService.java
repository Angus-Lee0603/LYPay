package com.lee.pay.config.service;

import com.lee.pay.config.entity.PayConfig;
import com.lee.pay.enums.PayMethod;

import java.util.List;


public interface IPayConfigService {

    Boolean setConfig(PayMethod method, PayConfig config);

    <E> E queryPayConfig(Class<E> payConfig);

    List<PayConfig> queryPayConfigs();

}
