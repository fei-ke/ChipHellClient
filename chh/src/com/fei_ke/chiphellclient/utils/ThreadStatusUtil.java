package com.fei_ke.chiphellclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 判断帖子状态，已读未读
 *
 * @author fei-ke
 * @2014年6月28日
 */
public class ThreadStatusUtil {
    private static final String READ_STATUS = "READ_STATUS";
    private SharedPreferences preferences;

    public ThreadStatusUtil(Context context) {
        preferences = context.getSharedPreferences(READ_STATUS, Context.MODE_PRIVATE);
    }

    /**
     * 获取帖子状态
     *
     * @param tid
     * @return
     */
    public synchronized boolean isRead(String tid) {
        return preferences.getBoolean(tid, false);
    }

    /**
     * 设置帖子状态
     *
     * @param tid
     * @param read
     */
    public synchronized void setRead(String tid, boolean read) {
        preferences.edit().putBoolean(tid, read).commit();
    }

    /**
     * 将帖子标记为已读状态
     *
     * @param tid
     */
    public synchronized void setRead(String tid) {
        setRead(tid, true);
    }

}
