package com.zero.hycache.manager;

import com.zero.hycache.util.CacheType;

/**
 * @author zero
 * @date Create on 2022/8/15
 * @description
 */
public class HyCacheManager {

    // there will init hyCacheMap and methodNameMap when java compiler

    public static Object getCache(String key, int expire, String cacheType) {
        return CacheManager.getInstance().getCache(CacheType.valueOf(cacheType)).beforeMethod(key, expire);
    }

    public static void addCache(String key, int expire, String cacheType, Object result) {
        CacheManager.getInstance().getCache(CacheType.valueOf(cacheType)).afterMethod(key, expire, result);
    }
}
