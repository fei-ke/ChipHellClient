
package com.fei_ke.chiphellclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.fei_ke.chiphellclient.ChhApplication;
import com.fei_ke.chiphellclient.R;

/**
 * 全局设置
 *
 * @author fei-ke
 * @2014年6月28日
 */
public class GlobalSetting {
    private static final String SETTING = "setting";
    public static final String SWIPE_BACK_EDGE = "swipe_back_edge";
    public static final String FORUM_ADDRESS = "forum_address";
    public static final String DEFAULT_FORUM_ADDRESS;

    private static SharedPreferences mPreferences;

    static {
        DEFAULT_FORUM_ADDRESS = ChhApplication.getInstance().getString(R.string.default_forum_address);
        mPreferences = ChhApplication.getInstance().getSharedPreferences(SETTING, Context.MODE_PRIVATE);
    }

    public static int getSwipeBackEdge() {
        return mPreferences.getInt(SWIPE_BACK_EDGE, 1);
    }

    public static void putSwipeBackEdge(int edge) {
        putInt(SWIPE_BACK_EDGE, edge);
    }

    public static String getForumAddress() {
        return mPreferences.getString(FORUM_ADDRESS, DEFAULT_FORUM_ADDRESS);
    }

    public static void setForumAddress(String forumAddress) {
        putString(FORUM_ADDRESS, forumAddress);
    }

    private static void putInt(String key, int value) {
        mPreferences.edit().putInt(key, value).apply();
    }

    private static void putString(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
    }
}
