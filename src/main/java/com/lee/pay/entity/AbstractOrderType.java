package com.lee.pay.entity;


import com.lee.pay.utils.enumUtil.BaseEnum;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description TODO
 * @author lee yee91300@gmail.com
 * @create 2022/9/30 23:21
 */
public class AbstractOrderType extends BaseEnum {
 
	private static final Map<String, AbstractOrderType> NAME_ENUM_MAP = new ConcurrentHashMap<>();
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
		if (!NAME_ENUM_MAP.containsKey(name)) {
			NAME_ENUM_MAP.put(name, this);
		}
	}

}