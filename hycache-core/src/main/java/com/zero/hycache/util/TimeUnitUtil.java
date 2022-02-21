package com.zero.hycache.util;

import com.zero.hycache.exception.IllegalTimeUnitException;

import java.util.concurrent.TimeUnit;

/**
 * @author zero
 * @date Create on 2020/12/12
 * @description
 */
public class TimeUnitUtil {

    public static long getSeconds(int time, TimeUnit timeUnit) {
        long unit;
        switch (timeUnit) {
            case SECONDS:
                unit = 1L;
                break;
            case MINUTES:
                unit = 60L;
                break;
            case HOURS:
                unit = 60 * 60L;
                break;
            case DAYS:
                unit = 60 * 60 * 24L;
                break;
            default:
                throw new IllegalTimeUnitException();
        }
        return time * unit;
    }

}
