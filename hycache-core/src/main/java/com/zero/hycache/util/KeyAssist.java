package com.zero.hycache.util;

import com.zero.hycache.exception.IllegalKeyParameterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zero
 * @date Create on 2022/2/25
 * @description
 */
public class KeyAssist {

    public static String getNoArgsKey(String keyName, String methodName) {
        return StringUtils.isBlank(keyName) ? methodName : keyName;
    }

    /**
     * @param keyPro
     * @param objects do not support Variable parameter,so replace by arrays
     * @return
     */
    public static String getArgsKey(String keyPro, String indexStr, Object[] objects) {
        List list = StringUtils.splitByCharAt(indexStr, ',');
        StringBuilder sb = new StringBuilder(keyPro);
        for (int i = 0; i < list.size(); i++) {
            sb.append(objects[i]);
        }
        return sb.toString();
    }

    // only support basic args type
    //TODO 检验类型
    public static List<String> getArgsIndex(String keyName, List<String> paramList) {
        if (!keyName.contains("#")) {
            return null;
        }
        if (CollectionUtils.isEmpty(paramList)) {
            throw new IllegalKeyParameterException("Illegal key parameter,there is no parameter in the method");
        }

        String[] keyArgs = keyName.split("#");
        // remove first index and copy to new target args
        String[] targetArgs = new String[keyArgs.length - 1];
        System.arraycopy(keyArgs, 1, targetArgs, 0, targetArgs.length);
        Map<String, Integer> paramMap = CollectionUtils.listToMap(paramList);
        List<String> list = new ArrayList<>();
        // check key param
        for (String targetArg : targetArgs) {
            if (!paramList.contains(targetArg)) {
                throw new IllegalKeyParameterException(String.format("Illegal key parameter,parameter [%s] must Existing in the method", targetArg));
            } else {
                list.add((paramMap.get(targetArg) + ""));
            }
        }
        return list;
    }

    public static boolean isIllegalFormat(String keyName) {
        if (StringUtils.isBlank(keyName)) {
            return false;
        }
        if (keyName.startsWith("#") || keyName.endsWith("#")) {
            return true;
        }
        return false;
    }

}
