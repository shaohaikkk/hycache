package com.zero.hycache.exception;

/**
 * @author zero
 * @date Create on 2022/2/24
 * @description
 */
class HyCacheException extends RuntimeException{
    private HyCacheException(){}

    public HyCacheException(String message) {
        super(message);
    }
}
