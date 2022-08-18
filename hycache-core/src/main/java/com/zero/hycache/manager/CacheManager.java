package com.zero.hycache.manager;

import com.zero.hycache.strategy.AspectCache;
import com.zero.hycache.strategy.CaffeineCache;
import com.zero.hycache.util.CacheType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zero
 * @date Create on 2020/12/12
 * @description TODO
 */
public class CacheManager {

    private static CacheManager INSTANCE = null;
    private static Map<CacheType, AspectCache> cacheTypeMap = new HashMap<>();

    static {
        cacheTypeMap.put(CacheType.LOCAL, new CaffeineCache());
    }

    private CacheManager() {
    }

    /**
     * single instance
     *
     * @return
     */
    public static CacheManager getInstance() {
        if (INSTANCE == null) {
            synchronized (CacheManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CacheManager();
                }
            }
        }
        return INSTANCE;
    }

    public AspectCache getCache(CacheType cacheType) {
        return cacheTypeMap.get(cacheType);
    }

}
