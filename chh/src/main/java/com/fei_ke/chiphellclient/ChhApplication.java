package com.fei_ke.chiphellclient;

import android.app.Application;
import android.text.TextUtils;

import com.fei_ke.chiphellclient.api.support.WebViewCookieHandler;
import com.fei_ke.chiphellclient.utils.LogMessage;

import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

public class ChhApplication extends Application {
    private static ChhApplication instance;
    private String formHash;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        LogMessage.setDebug(BuildConfig.DEBUG);

        initImageLoader();

        setupUpdate();
    }

    private void setupUpdate() {

    }


    public String getFormHash() {
        return formHash;
    }

    public void setFormHash(String formHash) {
        this.formHash = formHash;
    }

    // 初始化ImageLoader
    private void initImageLoader() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .cookieJar(new JavaNetCookieJar(WebViewCookieHandler.getInstance()))
                .build();

    }


    public static ChhApplication getInstance() {
        return instance;
    }

    /**
     * 是否登录
     *
     * @return
     */
    public boolean isLogin() {
        return !TextUtils.isEmpty(formHash);
    }


}
