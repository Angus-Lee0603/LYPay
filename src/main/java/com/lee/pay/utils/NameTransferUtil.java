package com.lee.pay.utils;

import org.apache.commons.lang3.StringUtils;

public class NameTransferUtil {

    public static String deCamelize(String camelCaseStr) {
        return StringUtils.isBlank(camelCaseStr) ? camelCaseStr : camelCaseStr.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    public static String classNameToTableName(String className) {
        String camel = (className.charAt(0) + "").toLowerCase() + className.substring(1);
        return deCamelize(camel);
    }


}
