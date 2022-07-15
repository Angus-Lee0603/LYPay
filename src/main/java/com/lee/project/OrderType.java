package com.lee.project;


import com.lee.pay.entity.AbstractOrderType;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderType extends AbstractOrderType {

    private static final Map<String, AbstractOrderType> nameEnumMap = new ConcurrentHashMap<>();

    public static final Map<Integer, OrderType> typesByValue = new HashMap<>();

    @Getter
    public final int pendingTime;

    protected OrderType(String name, int code, String value, String tableName, int pendingTime) {
        super(name, code, value, tableName);
        this.pendingTime = pendingTime;

        if (!nameEnumMap.containsKey(name)) {
            nameEnumMap.put(name, this);
            typesByValue.put(code, this);
        }
    }

    public static AbstractOrderType forValue(Integer code) {
        return typesByValue.get(code);
    }


    public static final OrderType TESTORDER = new OrderType("TESTORDER", 1, "测试订单", "test_order", 1);
}
