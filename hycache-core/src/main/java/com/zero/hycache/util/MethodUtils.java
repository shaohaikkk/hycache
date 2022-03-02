package com.zero.hycache.util;

import javassist.CtMethod;

import java.lang.reflect.Modifier;

/**
 * @author zero
 * @date Create on 2022/2/28
 * @description
 */
public class MethodUtils {
    public static String getFullMethodName(CtMethod ctMethod) {
        return Modifier.toString(ctMethod.getModifiers()) + " " + ctMethod.getLongName();
    }
}
