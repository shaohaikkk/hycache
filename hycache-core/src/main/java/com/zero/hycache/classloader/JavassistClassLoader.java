package com.zero.hycache.classloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zero
 * @date Create on 2021/12/23
 * @description
 */
public class JavassistClassLoader extends ClassLoader {

    private Set<String> loadedClass=new HashSet<>();

    public JavassistClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // 优先使用当前classloader 加载，加载不到，则委托父类加载
        Class<?> aClass = this.findClass(name);
        if (aClass != null) {
            return aClass;
        }
        return super.loadClass(name);
    }

    @Override
    public Class<?> findClass(String name) {
        // Avoid repeated scanning
        if(loadedClass.contains(name)){
            return null;
        }
        String path = this.getClass().getClassLoader().getResource("").getPath();
        String filePath = path + name.replaceAll("\\.", "\\" + File.separator).concat(".class");
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        // add to loaded class set
        loadedClass.add(name);
        try (FileInputStream fis = new FileInputStream(file); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, b);
            }
            byte[] bytes = bos.toByteArray();
            return defineClass(name, bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把CLASS文件转成BYTE
     *
     * @throws Exception
     */
    private byte[] getClassFileBytes(File file) throws Exception {
        //采用NIO读取
        FileInputStream fis = new FileInputStream(file);
        FileChannel fileC = fis.getChannel();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WritableByteChannel outC = Channels.newChannel(baos);
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        while (true) {
            int i = fileC.read(buffer);
            if (i == 0 || i == -1) {
                break;
            }
            buffer.flip();
            outC.write(buffer);
            buffer.clear();
        }
        fis.close();
        return baos.toByteArray();
    }

    private byte[] getClassBytes(String filePath) throws Exception {
        // 这里要读入.class的字节，因此要使用字节流
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        WritableByteChannel wbc = Channels.newChannel(bos);
        ByteBuffer by = ByteBuffer.allocate(1024);

        while (true) {
            int i = fc.read(by);
            if (i == 0 || i == -1)
                break;
            by.flip();
            wbc.write(by);
            by.clear();
        }
        fis.close();
        return bos.toByteArray();
    }

}
