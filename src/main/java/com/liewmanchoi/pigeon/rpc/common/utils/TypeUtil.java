package com.liewmanchoi.pigeon.rpc.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangsheng
 * @date 2019/6/26
 */
public class TypeUtil {
    private static Map<String, Class> CLASS_MAP = new HashMap<>();

    static {
        CLASS_MAP.put("int", int.class);
        CLASS_MAP.put("float", float.class);
        CLASS_MAP.put("double", double.class);
        CLASS_MAP.put("boolean", boolean.class);
        CLASS_MAP.put("byte", byte.class);
        CLASS_MAP.put("char", char.class);
        CLASS_MAP.put("short", short.class);
        CLASS_MAP.put("long", long.class);
    }
    
    public static boolean isPrimitive(String type) {
        return CLASS_MAP.containsKey(type);
    }
    
    
    public static Class map(String type) {
        return CLASS_MAP.get(type);
    }
}
