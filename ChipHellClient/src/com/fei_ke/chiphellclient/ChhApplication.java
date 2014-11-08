package com.fei_ke.chiphellclient;

import android.app.Application;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.webkit.CookieSyncManager;

import com.fei_ke.chiphellclient.utils.LogMessage;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.update.UmengUpdateAgent;

public class ChhApplication extends Application {
    private static ChhApplication instance;
    private String formHash;

    @Override
    public void onCreate() {
        super.onCreate();

        CookieSyncManager.createInstance(this);

        instance = this;

        LogMessage.setDebug(BuildConfig.DEBUG);

        initImageLoader();
        try {
            brandGlowEffect(this, getResources().getColor(R.color.chh_red));
        } catch (Exception e) {
            e.printStackTrace();
        }

        umeng();
    }

    private void umeng() {
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
    }

    public String getFormHash() {
        return formHash;
    }

    public void setFormHash(String formHash) {
        this.formHash = formHash;
    }

    // 初始化ImageLoader
    private void initImageLoader() {
        DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .showImageOnLoading(R.drawable.logo)
                        // .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        // .memoryCache(new LRULimitedMemoryCache(40 * 1024 * 1024))
                        // .writeDebugLogs() // Remove for release app
                .defaultDisplayImageOptions(defaultDisplayImageOptions)
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
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

    static void brandGlowEffect(Context context, int brandColor) {
        // glow
        int glowDrawableId = context.getResources().getIdentifier("overscroll_glow", "drawable", "android");
        Drawable androidGlow = context.getResources().getDrawable(glowDrawableId);
        androidGlow.setColorFilter(brandColor, PorterDuff.Mode.SRC_IN);
        // edge
        int edgeDrawableId = context.getResources().getIdentifier("overscroll_edge", "drawable", "android");
        Drawable androidEdge = context.getResources().getDrawable(edgeDrawableId);
        androidEdge.setColorFilter(brandColor, PorterDuff.Mode.SRC_IN);
    }
}
