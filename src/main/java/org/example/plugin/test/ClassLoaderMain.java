package org.example.plugin.test;


import org.example.plugin.classloader.ClassModifyWatch;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description: ClassLoaderMain
 * date: 2021/10/12 15:47
 * author: fangjie24
 */
public class ClassLoaderMain {

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new ClassModifyWatch(), 5, 10, TimeUnit.SECONDS);
    }
}
