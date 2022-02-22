package com.zero.hycache.manager;

import com.zero.hycache.annotation.HyCache;
import com.zero.hycache.util.StringUtils;
import com.zero.hycache.util.TimeUnitUtil;

/**
 * @author zero
 * @date Create on 2020/12/12
 * @description
 */
public class CacheKeyStrategy {

    /**
     * get cache key
     * if existed  declare key used declare key
     * else used full path method
     * @param methodName
     * @param key
     * @return methodName
     */
    public static String getKey(String methodName, String key) {
        if (StringUtils.isNotBlank(key)) {
            return key;
        }
        //full path method as to key
        //TODO need update annotation value to avoid frequently use java reflect
        return  methodName;
    }

    /**
     * used expire time as client key
     * @param cache
     * @return
     */
    public static long getClientKey(HyCache cache){
        return TimeUnitUtil.getSeconds(cache.expire(), cache.timeUnit());
    }
}
