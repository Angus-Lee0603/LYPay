package com.lee.pay.utils.enumUtil;

import com.lee.pay.entity.AbstractOrderType;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseEnum {

    private static final Map<String, BaseEnum> nameEnumMap = new ConcurrentHashMap<>();

    @Getter
    public String name;


    protected BaseEnum(String name) {
        this.name = name;
        if (!nameEnumMap.containsKey(name)) {
            nameEnumMap.put(name, this);
        }
    }

    public boolean equals(BaseEnum baseEnum) {
        return this.name != null && baseEnum != null && this.name.equals(baseEnum.getName());
    }

    public String toString() {
        return this.name;
    }

//    private static BaseEnum valueOf(String name) {
//        if (name == null)
//            throw new NullPointerException("Name is null");
//
//        BaseEnum result = nameEnumMap.get(name);
//        if (result != null) {
//            return result;
//        }
//
//        throw new IllegalArgumentException(
//                "No enum constant exists, name is." + name);
//    }

    public static void init() {
    }

    public static AbstractOrderType forValue(Integer code) {
        return null;
    }



}