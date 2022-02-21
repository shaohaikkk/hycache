package com.zero.hycache.strategy;

import com.github.benmanes.caffeine.cache.Cache;
import com.zero.hycache.manager.CacheClientManager;
import com.zero.hycache.manager.CacheKeyStrategy;

/**
 * @author zero
 * @date Create on 2020/10/21
 * @description
 */
public class CaffeineCache implements AspectCache {


    @Override
    public Object beforeMethod(String methodName, String key, int expire, String cacheType) {
        Cache<String, Object> cacheClient = CacheClientManager.getInstance().getClient(expire);
        String cacheKey = CacheKeyStrategy.getKey(methodName, key);
        //TODO refresh code
        return cacheClient.getIfPresent(cacheKey);
    }

    @Override
    public void afterMethod(String methodName, String key, int expire, String cacheType, Object result) {
        // get client key
        Cache<String, Object> cacheClient = CacheClientManager.getInstance().getClient(expire);
        // get key
        String cacheKey = CacheKeyStrategy.getKey(methodName, key);
        cacheClient.put(cacheKey, result);
    }
}
