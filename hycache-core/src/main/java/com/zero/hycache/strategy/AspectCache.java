package com.zero.hycache.strategy;

/**
 * @author zero
 * @date Create on 2020/10/21
 * @description
 */
public interface AspectCache {
    Object beforeMethod(String methodName, String key, int expire, String cacheType);

    void afterMethod(String methodName, String key, int expire, String cacheType, Object result);
}
