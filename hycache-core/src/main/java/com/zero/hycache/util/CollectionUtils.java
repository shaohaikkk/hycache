package com.zero.hycache.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zero
 * @date Create on 2022/2/28
 * @description
 */
public class CollectionUtils {

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean isNotEmpty(List<T> list) {
        return !isEmpty(list);
    }

    public static <T> Map<T, Integer> listToMap(List<T> list) {
        Map<T, Integer> map = new HashMap<>();
        if (list == null) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i), i);
        }
        return map;
    }
}
