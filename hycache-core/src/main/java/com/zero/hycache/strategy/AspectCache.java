package com.zero.hycache.strategy;

/**
 * @author zero
 * @date Create on 2020/10/21
 * @description
 */
public interface AspectCache {
    Object beforeMethod(String key, int expire);

    void afterMethod(String key, int expire, Object result);
}
