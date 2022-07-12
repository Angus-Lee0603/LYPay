package com.lee.pay.enums;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum OrderFrom {

    WX_MINI_PROGRAM("小程序", 1),
    OTHER("其他", 2);


    public static final Map<Integer, OrderFrom> typesByCode = new HashMap<>();

    static {
        for (OrderFrom balanceStatus : OrderFrom.values()) {
            typesByCode.put(balanceStatus.code, balanceStatus);
        }
    }

    @Getter
    public String label;
    @Getter
    public int code;

    OrderFrom(String label, int code) {
        this.label = label;
        this.code = code;

    }

    public static OrderFrom forValue(Integer code) {
        return typesByCode.get(code);
    }

}
