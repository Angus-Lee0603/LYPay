package com.lee.pay.utils.enumUtil;

import java.util.Map;

public interface EnumImporter<T> {
     Map<Integer,?extends T> getEnum();
}

