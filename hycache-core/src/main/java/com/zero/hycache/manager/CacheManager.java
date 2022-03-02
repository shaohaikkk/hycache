package com.zero.hycache.manager;

import com.zero.hycache.annotation.HyCache;
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

    private static volatile CacheManager INSTANCE = null;
    private static Map<CacheType, AspectCache> cacheTypeMap = new HashMap<>();
    private static Map<String, HyCache> annotationMap = new HashMap<>();

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

    public AspectCache getAspectCache(String type) {
        CacheType cacheType = CacheType.valueOf(type);
        return cacheTypeMap.get(cacheType);
    }

    public HyCache getAnnotationByMethod(String method) {
        return annotationMap.get(method);
    }

    public void putAnnotation(String method, HyCache hyCache) {
        annotationMap.put(method, hyCache);
    }

    public void clearAnnotationMap() {
        // help GC
        annotationMap=new HashMap<>();
    }

}
