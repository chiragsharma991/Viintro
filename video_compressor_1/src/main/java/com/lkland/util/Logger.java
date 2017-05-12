package com.lkland.util;

/**
 * Created by david on 4/29/15.
 */
import android.util.Log;

public class Logger {
    public final static String PACKAGE_NAME = "com.lkland.videocompressor";
    public final static String LOG_TAG = "videocompressor_log";
    public static void log(String msg){
        Thread current = Thread.currentThread();
        StackTraceElement[] stack = current.getStackTrace();
        for(StackTraceElement element : stack)
        {
            if (!element.isNativeMethod()) {
                if(element.getClassName().contains((PACKAGE_NAME))&&!element.getClassName().contains("Logger")) {
                    String className = element.getClassName().replace(PACKAGE_NAME+".","");
                    String fileName = element.getFileName();
                    int lineNumber = element.getLineNumber();
                    String methodName = element.getMethodName();
                    Log.e(LOG_TAG,className+"."+methodName+":"+String.valueOf(msg));
                    return;
                }
            }
        }
    }

    public static void log(int msg){
        Thread current = Thread.currentThread();
        StackTraceElement[] stack = current.getStackTrace();
        for(StackTraceElement element : stack)
        {
            if (!element.isNativeMethod()) {
                if(element.getClassName().contains((PACKAGE_NAME))&&!element.getClassName().contains("Logger")) {
                    String className = element.getClassName().replace(PACKAGE_NAME+".","");
                    String fileName = element.getFileName();
                    int lineNumber = element.getLineNumber();
                    String methodName = element.getMethodName();
                    Log.e(LOG_TAG,className+"."+methodName+":"+String.valueOf(msg));
                    return;
                }
            }
        }
    }

    public static void log(Exception e){
        Thread current = Thread.currentThread();
        StackTraceElement[] stack = current.getStackTrace();
        for(StackTraceElement element : stack){
            if (!element.isNativeMethod()) {
                if(element.getClassName().contains((PACKAGE_NAME))&&!element.getClassName().contains("Logger")) {
                    String className = element.getClassName().replace(PACKAGE_NAME+".","");
                    String fileName = element.getFileName();
                    int lineNumber = element.getLineNumber();
                    String methodName = element.getMethodName();
                    Log.e(LOG_TAG,className+"."+methodName+":"+Log.getStackTraceString(e));
                    return;
                }
            }
        }
    }
}