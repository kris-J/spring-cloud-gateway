package org.example.plugin.test;

/**
 * description: ClassLoaderTestImpl
 * date: 2021/10/12 17:56
 * author: fangjie24
 */
public class ClassLoaderTestImpl implements ClassLoaderTestInterface{

    @Override
    public void test() {
        System.out.println("ClassLoaderTestImpl v1");
    }
}
