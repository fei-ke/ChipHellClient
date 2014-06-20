
package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.AttributeSet;
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

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * 帖子内的主贴
 * 
 * @author 杨金阳
 * @2014-6-16
 */
@EViewGroup(R.layout.layout_post_main)
public class PostMainView extends FrameLayout {
    @ViewById(R.id.imageView_avatar)
    ImageView imageViewAvatar;

    @ViewById(R.id.webView_content)
    WebView webViewContent;

    @ViewById(R.id.textView_title)
    TextView textViewTitle;

    @ViewById(R.id.textView_authi)
    TextView textViewAuthi;

    public static PostMainView newInstance(Context context) {
        return PostMainView_.build(context);
    }

    public PostMainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PostMainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PostMainView(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        webViewContent = (WebView) findViewById(R.id.webView_content);
        webViewContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                getContext().startActivity(intent);
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

    public void bindValue(String title, Post post) {
        ImageLoader.getInstance().displayImage(post.getAvatarUrl(), imageViewAvatar);
        textViewAuthi.setText(Html.fromHtml(post.getAuthi()));
        textViewTitle.setText(title);
        webViewContent.loadDataWithBaseURL(Constants.BASE_URL, post.getContent(), "text/html", "utf-8", null);
    }

}
