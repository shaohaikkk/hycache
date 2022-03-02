package com.zero.hycache.exception;

/**
 * @author zero
 * @date Create on 2020/12/12
 * @description
 */
public class IllegalKeyFormatException extends HyCacheException{

    public IllegalKeyFormatException() {
        super("Illegal key format,do not start with '#' and ends with '#'");
    }

    public IllegalKeyFormatException(String message) {
        super(message);
    }
}
