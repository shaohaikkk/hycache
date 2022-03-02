package com.zero.hycache.manager;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author zero
 * @date Create on 2020/12/12
 * @description
 */
public class CacheClientManager {

    private static CacheClientManager INSTANCE = null;
    private static final Map<Long, Cache<String, Object>> clientMap = new ConcurrentHashMap<>();

    /**
     * TODO need think how to solve the concurrent and performance
     *
     * @param key
     * @return
     */
    public Cache<String, Object> getExpireClient(long key) {
        // Never Expires
        if (key < 1) {
            key = -2;
        }
        Cache<String, Object> client = clientMap.get(key);
        if (client == null) {
            synchronized (CacheClientManager.class) {
                client = clientMap.get(key);
                if (client == null) {
                    // Never Expires
                    if (key < 1) {
                        client = Caffeine
                                .newBuilder()
                                .build();
                    } else {
                        client = Caffeine
                                .newBuilder()
                                .expireAfterWrite(key, TimeUnit.SECONDS).build();
                    }
                    clientMap.put(key, client);
                }
            }
        }
        return client;
    }

    private CacheClientManager() {
    }

    public static CacheClientManager getInstance() {
        if (INSTANCE == null) {
            synchronized (CacheClientManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CacheClientManager();
                }
            }
        }
        return INSTANCE;
    }
}
