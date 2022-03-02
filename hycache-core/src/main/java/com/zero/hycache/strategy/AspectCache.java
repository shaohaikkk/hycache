package com.zero.hycache.strategy;

/**
 * @author zero
 * @date Create on 2020/10/21
 * @description
 */
public interface AspectCache {
    Object beforeMethod(String key,String methodName);

    void afterMethod(String key,String methodName,Object result);
}
