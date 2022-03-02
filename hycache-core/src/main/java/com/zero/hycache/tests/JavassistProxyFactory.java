package com.zero.hycache.tests;

import com.zero.hycache.annotation.HyCache;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.lang.reflect.Method;

/**
 * @author zero
 * @date Create on 2022/3/2
 * @description
 */
public class JavassistProxyFactory<T> extends ProxyFactory {
    private T target;

    public JavassistProxyFactory(T target) {
        this.target = target;
    }

    //获取代理类
    public T getProxy() throws IllegalAccessException, InstantiationException {
        setSuperclass(target.getClass());
        setDefaultFilter();
        T proxy = (T) createClass().newInstance();
        ((ProxyObject) proxy).setHandler(getDefaultHandler());
        return proxy;
    }

    //设置默认的方法过滤器，如果此处不做过滤，代理类中将会把 Object 的基础方法都重写并生成对应的代理方法
    private void setDefaultFilter() {
        this.setFilter((Method m) -> {
            if (m.getAnnotation(HyCache.class) != null) {
                return true;
            }
            return false;
        });
    }

    //为代理类设置处理句柄
    private MethodHandler getDefaultHandler() {
        return (self, thisMethod, proceed, args) -> {
            System.out.println("before " + thisMethod.getName() + " execution...");
            Object result = thisMethod.invoke(target, args);
            System.out.println("after " + thisMethod.getName() + " execution...");
            return result;
        };
    }
}
