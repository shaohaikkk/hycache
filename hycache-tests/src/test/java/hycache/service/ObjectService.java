package hycache.service;

import com.zero.hycache.annotation.HyCache;
import com.zero.hycache.bean.UserBean;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zero
 * @date Create on 2022/8/20
 * @description
 */
public class ObjectService {

    private static AtomicInteger atomicInt = new AtomicInteger(1);
    private static AtomicInteger atomicIntger = new AtomicInteger(1);
    private static AtomicInteger age = new AtomicInteger(1);

    @HyCache
    public int testIncreInt() {
        return atomicInt.getAndIncrement();
    }

    @HyCache
    public Integer testIncreInteger() {
        return Integer.valueOf(atomicIntger.getAndIncrement() + "");
    }

//    @HyCache
    public UserBean testObject() {
        return new UserBean("tom", age.getAndIncrement());
    }

}
