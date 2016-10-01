package com.fei_ke.chiphellclient.analytics;


import com.fei_ke.chiphellclient.ChhApplication;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;

/**
 * Created by fei on 16/10/2.
 */

public class Analytics {
    public static void reportError(Throwable t) {
        MobclickAgent.reportError(ChhApplication.getInstance(), t);
    }

    public static void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    public static void onPause(Context context) {
        MobclickAgent.onPause(context);
    }
}
