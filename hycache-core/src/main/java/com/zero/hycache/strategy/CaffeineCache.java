package com.zero.hycache.strategy;

import com.github.benmanes.caffeine.cache.Cache;
import com.zero.hycache.manager.CacheClientManager;

/**
 * @author zero
 * @date Create on 2022/7/27
 * @description
 */
public class CaffeineCache implements AspectCache {

    public Object beforeMethod(String key, int expire) {
        Cache<String, Object> cacheClient = CacheClientManager.getInstance().getClient(expire);
        //TODO refresh code
        return cacheClient.getIfPresent(key);
    }

    public void afterMethod(String key, int expire, Object result) {
        // get client key
        Cache<String, Object> cacheClient = CacheClientManager.getInstance().getClient(expire);
        // get key
        cacheClient.put(key, result);
    }
}
