package com.zero.hycache.annotation;

import com.zero.hycache.util.CacheType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zero
 * @date Create on 2020/10/20
 * @description
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface HyCache {

    String key() default "";

    int expire() default -2;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    //TODO 暂不支持刷新
//    int refreshTime() default -2;
//
//    TimeUnit refreshTimeUnit() default TimeUnit.SECONDS;

    CacheType type() default CacheType.LOCAL;

    String descri() default "";

}
