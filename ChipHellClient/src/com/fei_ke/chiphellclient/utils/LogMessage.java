
package com.fei_ke.chiphellclient.utils;

import android.util.Log;

/**
 * 日志打印类
 */
public class LogMessage {

    // 是否打印日志
    private static boolean isDebug = true;
    // 日志标签
    public static String LOG_TAG = "frame";

    public static void v(String tag, Object msg) {
        if (isDebug) {
            Log.v(tag, msg != null ? msg.toString() : "null");
        }
    }

    public static void i(String tag, Object msg) {
        if (isDebug) {
            Log.i(tag, msg != null ? msg.toString() : "null");
        }
    }

    public static void d(String tag, Object msg) {
        if (isDebug) {
            Log.d(tag, msg != null ? msg.toString() : "null");
        }
    }

    public static void w(String tag, Object msg) {
        if (isDebug) {
            Log.w(tag, msg != null ? msg.toString() : "null");
        }
    }

    public static void e(String tag, Object msg) {
        if (isDebug) {
            Log.e(tag, msg != null ? msg.toString() : "null");
        }
    }

    public static void print(String tag, String msg) {
        // System.out.println("tag=="+msg);
    }

    /**
     * 设置debug 模式
     * 
     * @param isDebug true 打印日志 false：不打印
     */

    public static void setDebug(boolean isDebug) {
        LogMessage.isDebug = isDebug;
    }

}
