
package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.text.Html;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.constant.Post;
import com.nostra13.universalimageloader.core.ImageLoader;

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
        webViewContent.loadDataWithBaseURL(/* "file:///android_asset/" */Constants.BASE_URL, post.getContent(), "text/html", "utf-8",
                null);
        // textViewContent.setText(Html.fromHtml(post.getContent()));
    }

}
