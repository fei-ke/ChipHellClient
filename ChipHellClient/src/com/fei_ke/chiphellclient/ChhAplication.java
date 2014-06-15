
package com.fei_ke.chiphellclient;

import android.app.Application;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.utils.LogMessage;

public class ChhAplication extends Application {
    private String cookie;
    private static ChhAplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        CookieSyncManager.createInstance(this);
        String string = CookieManager.getInstance().getCookie(Constants.BASE_URL);
        setCookie(string);
        instance = this;
        LogMessage.setDebug(BuildConfig.DEBUG);
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public static ChhAplication getInstance() {
        return instance;
    }

}
