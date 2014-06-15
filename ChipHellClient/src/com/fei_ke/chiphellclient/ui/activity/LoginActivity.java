
package com.fei_ke.chiphellclient.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fei_ke.chiphellclient.ChhAplication;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.constant.Constants;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.cookie.Cookie;

/**
 * 登录页面
 * 
 * @author 杨金阳
 * @2014-6-15
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
    @ViewById(R.id.webView)
    WebView mWebView;

    public static Intent getStartIntent(Context context) {
        return LoginActivity_.intent(context).get();
    }

    @Override
    protected void onAfterViews() {
        mWebView.loadUrl("http://www.chiphell.com/member.php?mod=logging&action=login&mobile=2");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String string = CookieManager.getInstance().getCookie(Constants.BASE_URL);
                ChhAplication.getInstance().setCookie(string);
                System.out.println(string);

            }
        });
    }

}
