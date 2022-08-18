package com.zero.hycache.util;

import com.zero.hycache.exception.IllegalTimeUnitException;

import java.util.concurrent.TimeUnit;

/**
 * @author zero
 * @date Create on 2020/12/12
 * @description
 */
public class TimeUnitUtil {

    public static int getSeconds(int time, TimeUnit timeUnit) {
        int unit;
        switch (timeUnit) {
            case SECONDS:
                unit = 1;
                break;
            case MINUTES:
                unit = 60;
                break;
            case HOURS:
                unit = 60 * 60;
                break;
            case DAYS:
                unit = 60 * 60 * 24;
                break;
            default:
                throw new IllegalTimeUnitException();
        }
        return time * unit;
    }

}
