package com.zero.hycache.exception;

/**
 * @author zero
 * @date Create on 2020/12/12
 * @description
 */
public class IllegalTimeUnitException extends RuntimeException{

    public IllegalTimeUnitException() {
        super("Unsupported time format,only support SECONDS|MINUTES|HOURS|DAYS");
    }

    public IllegalTimeUnitException(String message) {
        super(message);
    }
}
