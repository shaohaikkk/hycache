package com.zero.hycache.enhance;

import com.zero.hycache.annotation.HyCache;
import com.zero.hycache.classloader.JavassistClassLoader;
import com.zero.hycache.util.TimeUnitUtil;
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
import java.util.stream.Collectors;

/**
 * @author zero
 * @date Create on 2022/2/16
 * @description
 */
public class HyCacheEnhancer {
    private static final String HY_CACHE_FREEZE_FLAG = "JAVASISST_HY_CACHE_FREEZED";

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
        JavassistClassLoader javassistClassLoader = new JavassistClassLoader(contextClassLoader);
        Thread.currentThread().setContextClassLoader(javassistClassLoader);
        Reflections reflections = new Reflections("", new MethodAnnotationsScanner());
        Set<Method> targetMethods = reflections.getMethodsAnnotatedWith(HyCache.class);
        Thread.currentThread().setContextClassLoader(contextClassLoader);
        ClassPool cp = ClassPool.getDefault();
        for (Method method : targetMethods) {
            Class<?> declaringClass = method.getDeclaringClass();
            CtClass targetClass = cp.getOrNull(declaringClass.getName());
            for (CtMethod targetCtMethod : targetClass.getMethods()) {
                if (targetCtMethod.hasAnnotation(HyCache.class)) {
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
            CtField ctField;ctField = new CtField(CtClass.booleanType, HY_CACHE_FREEZE_FLAG, ctClass);
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
        copy.setName(ctMethod.getName() + "$$HyCacheReal");
        ctClass.addMethod(copy);
        // 原方法进行增强
        String[] methodParamNames = getMethodParamNames(copy);
        List<String> list = Arrays.asList(methodParamNames);
        String params = list.stream().collect(Collectors.joining(","));
        HyCache hyCache = (HyCache) ctMethod.getAnnotation(HyCache.class);
        String methodName = ctMethod.getName();
        StringBuilder sb = new StringBuilder();
        // 方法调用前处理获取缓存
        sb.append("{");
        // 前置处理
        sb.append(String.format("Object obj = com.zero.hycache.manager.CacheManager.getInstance().getCache(\"%s\").beforeMethod(\"%s\",\"%s\",%s,\"%s\");\n", hyCache.type().name(), methodName, hyCache.key(), TimeUnitUtil.getSeconds(hyCache.expire(), hyCache.timeUnit()), hyCache.type().name()));
        sb.append("if(obj!=null){ return obj; }\n");
        //调用真实方法
        sb.append(String.format("Object result=this.%s(%s); \n", copy.getName(), params));
        // 后置处理
        sb.append(String.format("com.zero.hycache.manager.CacheManager.getInstance().getCache(\"%s\").afterMethod(\"%s\",\"%s\",%s,\"%s\",result);\n", hyCache.type().name(), methodName, hyCache.key(), TimeUnitUtil.getSeconds(hyCache.expire(), hyCache.timeUnit()), hyCache.type().name()));
        sb.append("return result;\n");
        sb.append("}");
        ctMethod.setBody(sb.toString());
        return true;
    }

    protected static String[] getMethodParamNames(CtMethod cm) throws NotFoundException {
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
