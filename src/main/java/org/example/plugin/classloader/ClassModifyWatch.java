package org.example.plugin.classloader;


import org.example.plugin.test.ClassLoaderTest;
import org.example.plugin.test.ClassLoaderTestInterface;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * description: ClassModifyWatch
 * date: 2021/10/12 15:48
 * author: fangjie24
 */
public class ClassModifyWatch implements Runnable {

    private Map<String, Long> fileModifyTime = new HashMap<>();

    @Override
    public void run() {
        ClassLoaderTest classLoaderTest = new ClassLoaderTest();
        classLoaderTest.test();
        File filePath = new File("D:\\classloader");
        File[] files = filePath.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                long currTime = file.lastModified();
                Long modifyTime = fileModifyTime.get(file.getName());
                if (modifyTime == null || modifyTime != currTime) {
                    loadMyInterfaceClass();
                    fileModifyTime.put(file.getName(), currTime);
                }
            }
        }
    }

    private void loadMyClass() {
        try {
            CustomClassLoader classLoader = new CustomClassLoader();
            Class<?> aClass = classLoader.loadClass("org.example.test.ClassLoaderTest1");
            Object test = aClass.newInstance();
            Method method = aClass.getMethod("test");
            method.invoke(test);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMyInterfaceClass() {
        try {
            CustomClassLoader classLoader = new CustomClassLoader();
            Class<?> aClass = classLoader.loadClass("org.example.test.ClassLoaderTestImpl1");
            ClassLoaderTestInterface classLoaderTestInterface = (ClassLoaderTestInterface) aClass.newInstance();
            classLoaderTestInterface.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
