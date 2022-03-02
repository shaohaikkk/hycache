package com.zero.hycache.tests;

/**
 * @author zero
 * @date Create on 2022/3/2
 * @description
 */
public class Tests {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        JavassistProxyFactory<Dog> proxyFactory = new JavassistProxyFactory<>(new Dog());
        proxyFactory.setUseWriteReplace(true);
        Dog proxy = proxyFactory.getProxy();
        proxy.say();
    }

}
