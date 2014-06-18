
package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.constant.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * 帖子内的一个item
 * 
 * @author 杨金阳
 * @2014-6-16
 */
@EViewGroup(R.layout.layout_post_item)
public class PostItemView extends FrameLayout {
    @ViewById(R.id.imageView_avatar)
    ImageView imageViewAvatar;

    @ViewById(R.id.webView_content)
    WebView webViewContent;

    @ViewById(R.id.textView_content)
    TextView textViewContent;

    public static PostItemView getInstance(Context context) {
        return PostItemView_.build(context);
    }

    public PostItemView(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        webViewContent = (WebView) findViewById(R.id.webView_content);
        webViewContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewContent.setWebChromeClient(new WebChromeClient());

        WebSettings settings = webViewContent.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webViewContent.setVerticalScrollBarEnabled(true);
        webViewContent.setHorizontalScrollBarEnabled(false);
        settings.setJavaScriptEnabled(true);
    }

    public void bindValue(Post post) {
        ImageLoader.getInstance().displayImage(post.getAvatarUrl(), imageViewAvatar);
        // webViewContent.loadDataWithBaseURL(/* "file:///android_asset/" */Constants.BASE_URL, post.getContent(), "text/html", "utf-8",
        // null);
        loadContent(post);
    }

    // @Background
    void loadContent(Post post) {
        textViewContent.setText(Html.fromHtml(post.getContent(), new ImageGetter() {

            @Override
            public Drawable getDrawable(String source) {
                if (!source.startsWith("http:")) {
                    source = Constants.BASE_URL + source;
                }
                System.out.println(source);
                return getContext().getResources().getDrawable(R.drawable.logo);
                // return new UrlDrawable(source, textViewContent);
            }
        }, null));
    }

    public static class UrlDrawable extends BitmapDrawable {
        protected Drawable drawable;
        View container;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        public UrlDrawable(String url, View container) {
            this.container = container;
            setBounds(0, 0, 30, 30);
            ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String arg0, View arg1) {
                }

                @Override
                public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                }

                @Override
                public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
                    BitmapDrawable drawable = new BitmapDrawable(bitmap);
                    setBounds(0, 0, 0 + bitmap.getWidth(), 0
                            + bitmap.getHeight());
                    UrlDrawable.this.drawable = drawable;

                    // redraw the image by invalidating the container
                    UrlDrawable.this.container.invalidate();
                }

                @Override
                public void onLoadingCancelled(String arg0, View arg1) {

                }
            });
        }
    }
}
