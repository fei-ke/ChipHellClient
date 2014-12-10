
package com.fei_ke.chiphellclient.ui.customviews;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 一个异步加载的Drawable
 * 
 * @author fei-ke
 *         2014-6-26 下午2:17:27
 */
public class UrlDrawable extends BitmapDrawable {
    protected Drawable drawable;
    private View container;

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    @SuppressWarnings("deprecation")
    public UrlDrawable(final String url, final View container) {
        this.container = container;
        setUrl(url);
    }

    public void setUrl(String url) {
        setBounds(0, 0, 100, 100);
        ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
            }

            @Override
            public void onLoadingComplete(String url, View arg1, Bitmap bitmap) {
                Drawable drawable = new BitmapDrawable(bitmap);
                int width = bitmap.getWidth() * 2;
                int height = bitmap.getHeight() * 2;
                drawable.setBounds(0, 0, width, height);
                UrlDrawable.this.drawable = drawable;
                setBounds(0, 0, width, height);

                container.invalidate();
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {

            }
        });
    }
}
