
package com.fei_ke.chiphellclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.fei_ke.chiphellclient.ChhApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局设置
 *
 * @author fei-ke
 * @2014年6月28日
 */
public class GlobalSetting {
    private static final String SETTING = "setting";
    public static final String SWIPE_BACK_EDGE = "swipe_back_edge";
    private static SharedPreferences mPreferences;
    private static Map<String, Object> cacheValue;
    static {
        mPreferences = ChhApplication.getInstance().getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        cacheValue = new HashMap<String, Object>();
    }

    public static int getSwipeBackEdge() {
        Integer edge = (Integer) cacheValue.get(SWIPE_BACK_EDGE);
        if (edge == null) {
            edge = mPreferences.getInt(SWIPE_BACK_EDGE, 1);
            cacheValue.put(SWIPE_BACK_EDGE, edge);
        }
        return edge;
    }

    public static void putSwipeBackEdge(int edge) {
        putInt(SWIPE_BACK_EDGE, edge);
    }

    private static void putInt(String key, int value) {
        mPreferences.edit().putInt(key, value).commit();
        cacheValue.put(key, value);
    }
}
