package com.zero.hycache.util;

import java.util.UUID;

/**
 * @author zero
 * @date Create on 2022/8/18
 * @description
 */
public class UuidUtils {

    public static String get32Id() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
