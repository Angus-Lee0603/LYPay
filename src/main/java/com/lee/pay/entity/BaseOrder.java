package com.lee.pay.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BaseOrder {

    public String orderId;

    public String userId;

    public BigDecimal realAmount;

    public String payMethod;
}
