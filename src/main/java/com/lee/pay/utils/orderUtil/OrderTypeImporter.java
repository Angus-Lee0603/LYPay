package com.lee.pay.utils.orderUtil;

import com.lee.pay.entity.AbstractOrderType;
import lombok.Getter;


import java.util.HashMap;
import java.util.Map;


public class OrderTypeImporter{

    @Getter
    public static final Map<Integer, AbstractOrderType> map = new HashMap<>();


    public static void setMap(Map<Integer, ? extends AbstractOrderType> sub) {
        for (Integer code : sub.keySet()) {
            map.put(code, sub.get(code));
        }
    }


}
