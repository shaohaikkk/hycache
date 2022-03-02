package com.zero.hycache.tests;

import com.zero.hycache.annotation.HyCache;

/**
 * @author zero
 * @date Create on 2022/3/2
 * @description
 */
public class Dog {

    @HyCache
    public void say(){
        System.out.println("wang~~");
    }

}
