package com.zero.hycache.exception;

/**
 * @author zero
 * @date Create on 2022/7/13
 * @description
 */
public class MethodNotSupportException extends RuntimeException{

    public MethodNotSupportException() {
        super("Methods without return values are not supported");
    }

    public MethodNotSupportException(String message) {
        super(message);
    }

    public MethodNotSupportException(String message, Throwable cause) {
        super(message, cause);
    }
}
