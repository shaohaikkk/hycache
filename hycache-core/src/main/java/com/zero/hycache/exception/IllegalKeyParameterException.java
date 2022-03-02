package com.zero.hycache.exception;

/**
 * @author zero
 * @date Create on 2020/12/12
 * @description
 */
public class IllegalKeyParameterException extends HyCacheException{

    public IllegalKeyParameterException() {
        super("Illegal key parameter,parameter must Existing in the method");
    }

    public IllegalKeyParameterException(String message) {
        super(message);
    }
}
