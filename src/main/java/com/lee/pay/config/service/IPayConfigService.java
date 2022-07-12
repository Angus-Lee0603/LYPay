package com.lee.pay.config.service;

import com.lee.pay.config.entity.PayConfig;
import com.lee.pay.enums.PayMethod;


public interface IPayConfigService {

    Boolean setConfig(PayMethod method, PayConfig config);

    <E> E queryPayConfig(Class<E> payConfig);

}
