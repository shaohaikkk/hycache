package com.zero.hycache.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zero
 * @date Create on 2020/12/12
 * @description
 */
public class StringUtils {
    private static final int STRING_BUILDER_SIZE = 256;
    public static final String SPACE = " ";
    public static final String EMPTY = "";
    public static final String LF = "\n";
    public static final String CR = "\r";
    public static final int INDEX_NOT_FOUND = -1;
    private static final int PAD_LIMIT = 8192;

    private StringUtils() {
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static List<String> splitByCharAt(String str, char regx) {
        //字符串截取的开始位置
        int begin = 0;
        //截取分割得到的字符串
        String splitStr = "";
        ArrayList<String> result = new ArrayList<String>();
        int length = str.length();
        //计数器
        int i = 0;
        for (i = 0; i < length;i++ ) {
            if (str.charAt(i) == regx) {
                splitStr = str.substring(begin, i);
                result.add(splitStr);
                str = str.substring(i + 1, length);
                length = str.length();
                i = 0;
            }
        }
        if (!StringUtils.isBlank(str)) {
            result.add(str);
        }
        return result;
    }
}
