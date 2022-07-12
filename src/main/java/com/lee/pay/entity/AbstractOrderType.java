package com.lee.pay.entity;


import com.lee.pay.utils.enumUtil.BaseEnum;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AbstractOrderType extends BaseEnum {
 
	private static final Map<String, AbstractOrderType> nameEnumMap = new ConcurrentHashMap<>();
	@Getter
	public final int code;
	@Getter
	public final String value;
	@Getter
	public final String tableName;
 
	protected AbstractOrderType(String name, int code, String value, String tableName) {
		super(name);
		this.code = code;
		this.value = value;
		this.tableName = tableName;
		if (!nameEnumMap.containsKey(name)) {
			nameEnumMap.put(name, this);
		}
	}

}