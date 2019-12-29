package com.fgan.azure.rx.util;

public class Utils {

    public static void print(String information, Class classObj, Thread thread) {
        String className = classObj.getSimpleName();
        String threadName = thread.getName();
        String message = String.format("Class(%s) - %s - Thread(%s)", className, information, threadName);
        System.out.println(message);
    }

    public static void sleepOneSecond() {
        final int MILLISECONDS = 1000;
        sleep(MILLISECONDS);
    }

    protected static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
