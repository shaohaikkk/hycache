package com.zero.hycache.strategy;

import com.github.benmanes.caffeine.cache.Cache;
import com.zero.hycache.annotation.HyCache;
import com.zero.hycache.manager.CacheClientManager;
import com.zero.hycache.manager.CacheManager;

/**
 * @author zero
 * @date Create on 2020/10/21
 * @description
 */
public class CaffeineCache implements AspectCache {


    @Override
    public Object beforeMethod(String key, String methodName) {
        HyCache hycache = CacheManager.getInstance().getAnnotationByMethod(methodName);
        int expire = hycache.expire() > 0 ? hycache.expire() : -2;
        Cache<String, Object> cacheClient = CacheClientManager.getInstance().getExpireClient(expire);
        return cacheClient.getIfPresent(key);
    }

    @Override
    public void afterMethod(String key, String methodName, Object result) {
        HyCache hycache = CacheManager.getInstance().getAnnotationByMethod(methodName);
        int expire = hycache.expire() > 0 ? hycache.expire() : -2;
        // get client key
        Cache<String, Object> cacheClient = CacheClientManager.getInstance().getExpireClient(expire);
        // get key
        cacheClient.put(key, result);
    }
}
