package com.zero.hycache.enhance;

import com.zero.hycache.annotation.HyCache;
import com.zero.hycache.classloader.JavassistClassLoader;
import com.zero.hycache.exception.IllegalKeyFormatException;
import com.zero.hycache.manager.CacheManager;
import com.zero.hycache.util.KeyAssist;
import com.zero.hycache.util.MethodUtils;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

/**
 * @author zero
 * @date Create on 2022/2/16
 * @description
 */
public class HyCacheEnhancer {
    private static final String HY_CACHE_FREEZE_FLAG = "JAVASISST_HY_CACHE_FREEZED";
    private static final String HY_CACHE_SUFFIX = "$$HyCache$$Agent";

    public static void start() {
        try {
            HyCacheEnhancer.doStart();
        } catch (CannotCompileException | NotFoundException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void doStart() throws CannotCompileException, NotFoundException, ClassNotFoundException, IOException {
        //get all annotations with HyCache by scan all packages
        Set<CtMethod> allCtMethods = new HashSet<>();
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        // use my define classloader
        JavassistClassLoader javassistClassLoader = new JavassistClassLoader(contextClassLoader);
        Thread.currentThread().setContextClassLoader(javassistClassLoader);
        Reflections reflections = new Reflections("", new MethodAnnotationsScanner());
        Set<Method> targetMethods = reflections.getMethodsAnnotatedWith(HyCache.class);
        // recovery context class loader
        Thread.currentThread().setContextClassLoader(contextClassLoader);
        ClassPool cp = ClassPool.getDefault();
        cp.importPackage("java.util");
        for (Method method : targetMethods) {
            Class<?> declaringClass = method.getDeclaringClass();
            CtClass targetClass = cp.getOrNull(declaringClass.getName());
            for (CtMethod targetCtMethod : targetClass.getMethods()) {
                if (targetCtMethod.hasAnnotation(HyCache.class)) {
                    // manage method
                    CacheManager.getInstance().putAnnotation(MethodUtils.getFullMethodName(targetCtMethod), (HyCache) targetCtMethod.getAnnotation(HyCache.class));
                    allCtMethods.add(targetCtMethod);
                }
            }
        }
        // get all class and method that with annotations HyCache
        Map<CtClass, Set<CtMethod>> classSetMap = new HashMap<>();
        for (CtMethod method : allCtMethods) {
            CtClass ctClass = cp.getOrNull(method.getDeclaringClass().getName());
            if (classSetMap.containsKey(ctClass)) {
                classSetMap.get(ctClass).add(method);
            } else {
                classSetMap.put(ctClass, new HashSet<>(Collections.singletonList(method)));
            }
        }

        // update Bytecode that there with @HyCache annotations
        for (CtClass ctClass : classSetMap.keySet()) {
            Set<CtMethod> methods = classSetMap.get(ctClass);
            if (isFrozen(ctClass)) {
                continue;
            }
            for (CtMethod ctMethod : methods) {
                enhanceMethod(ctMethod, ctClass);
            }
            // freeze the update class
            CtField ctField;
            ctField = new CtField(CtClass.booleanType, HY_CACHE_FREEZE_FLAG, ctClass);
            ctField.setModifiers(Modifier.PRIVATE);
            ctField.setModifiers(Modifier.FINAL);
            ctClass.addField(ctField);
            // write to class file
            URL resource = ctClass.getClass().getResource("/");
            // 初始化class
            ctClass.toClass();
            ctClass.writeFile(resource.getPath());
            // 写入class文件
            // 释放class
            ctClass.detach();
        }
    }


    private static boolean isFrozen(CtClass ctClass) {
        CtField[] fields = ctClass.getFields();
        for (CtField field : fields) {
            if (field.getName().equals(HY_CACHE_FREEZE_FLAG)) {
                return true;
            }
        }
        return false;
    }

    private static boolean enhanceMethod(CtMethod ctMethod, CtClass ctClass) throws CannotCompileException, NotFoundException, ClassNotFoundException {
        // 复制原方法重命名为新的真实方法
        CtMethod copy = CtNewMethod.copy(ctMethod, ctClass, null);
        copy.setName(ctMethod.getName() + HY_CACHE_SUFFIX);
        ctClass.addMethod(copy);
        // 原方法进行增强
        String[] methodParamNames = getMethodParamNames(copy);
        HyCache hyCache = (HyCache) ctMethod.getAnnotation(HyCache.class);
        if (KeyAssist.isIllegalFormat(hyCache.key())) {
            throw new IllegalKeyFormatException();
        }
        String keyPre = null;
        List<String> targetArgIndexList = null;
        String targetArgIndexStr=null;
        String methodName = MethodUtils.getFullMethodName(ctMethod);
        if (methodParamNames != null) {
            List<String> paramList = Arrays.asList(methodParamNames);
            targetArgIndexList = KeyAssist.getArgsIndex(hyCache.key(), paramList);
            if (targetArgIndexList != null) {
                keyPre = hyCache.key().split("#")[0];
                targetArgIndexStr = String.join(",", targetArgIndexList);
            }
        }
        StringBuilder sb = new StringBuilder();
        // 方法调用前处理获取缓存
        sb.append("{\n");
        sb.append(String.format("String methodName = \"%s\";\n", methodName));
        if (targetArgIndexList == null) {
            sb.append(String.format("String key = \"%s\";\n", KeyAssist.getNoArgsKey(hyCache.key(), methodName)));
        } else {
            // do not support array or varargs
            sb.append(String.format("String key = com.zero.hycache.util.KeyAssist.getArgsKey(\"%s\",\"%s\",$args);\n", keyPre,targetArgIndexStr));
        }
        // 前置处理
        sb.append(String.format("Object obj = com.zero.hycache.manager.CacheManager.getInstance().getAspectCache(\"%s\").beforeMethod(key,methodName);\n", hyCache.type().name()));
        sb.append("if(obj != null){ return obj; }\n");
        //调用真实方法
        if (methodParamNames == null) {
            sb.append(String.format("Object result = this.%s() ; \n", copy.getName()));
        } else {
            sb.append(String.format("Object result = this.%s($$) ; \n", copy.getName()));
        }
        // 后置处理
        sb.append(String.format("com.zero.hycache.manager.CacheManager.getInstance().getAspectCache(\"%s\").afterMethod(key, methodName, result);\n", hyCache.type().name()));
        sb.append("return ($r) result;\n");
        sb.append("}");
        ctMethod.setBody(sb.toString());
        return true;
    }


    private static String[] getMethodParamNames(CtMethod cm) throws NotFoundException {
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            return null;
        }
        String[] paramNames = new String[cm.getParameterTypes().length];
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos);
        }
        return paramNames;
    }
}
