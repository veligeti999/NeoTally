package com.newtally.core.util;

import java.util.HashMap;
import java.util.Map;

public class CollectionUtil {

    public static Map<String, Object> getSingleEntryMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);

        return map;
    }
}
