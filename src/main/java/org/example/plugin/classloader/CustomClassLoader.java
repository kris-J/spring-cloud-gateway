package org.example.plugin.classloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * description: CustomClassLoader
 * date: 2021/10/12 14:29
 * author: fangjie24
 */
public class CustomClassLoader extends ClassLoader {

    private static final String extFilePath = "d:/classloader";

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        //加载class二进制
        byte[] classByte = new byte[0];
        try {
            classByte = loadClassFromFile(name);
        } catch (FileNotFoundException e) {
            throw new ClassNotFoundException("class not found");
        }
        return defineClass(name, classByte, 0, classByte.length);
    }

    private byte[] loadClassFromFile(String name) throws FileNotFoundException {
        String fileName = name.substring(name.lastIndexOf(".")+1)+".class";
        File file = new File(extFilePath + File.separator + fileName);
        FileInputStream inputStream = new FileInputStream(file);
        ByteArrayOutputStream byteSt = new ByteArrayOutputStream();
        // 写入byteStream
        int len = 0;
        try {
            while ((len = inputStream.read()) != -1) {
                byteSt.write(len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 转换为数组
        return byteSt.toByteArray();
    }

    /**
     * 加载类数据
     *
     * @param name
     * @return
     */
    private byte[] loadClassFromClasspath(String name) throws ClassNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resource = classLoader.getResourceAsStream(name.replace(".", "/") + ".class");
        if (resource == null) {
            throw new ClassNotFoundException("class: "+ name +" not found");
        }
        ByteArrayOutputStream byteSt = new ByteArrayOutputStream();
        // 写入byteStream
        int len = 0;
        try {
            while ((len = resource.read()) != -1) {
                byteSt.write(len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 转换为数组
        return byteSt.toByteArray();
    }
}
