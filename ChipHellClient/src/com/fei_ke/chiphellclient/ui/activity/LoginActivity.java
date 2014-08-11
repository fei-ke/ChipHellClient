package com.fei_ke.chiphellclient.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.constant.Constants;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * 登录页面
 *
 * @author fei-ke
 * @2014-6-15
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
    @ViewById(R.id.webView)
    WebView mWebView;

    public static Intent getStartIntent(Context context) {
        return LoginActivity_.intent(context).get();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onAfterViews() {
        setTitle("登录");

        mWebView.getSettings().setJavaScriptEnabled(true);
        final String loginUrl = Constants.BASE_URL + "member.php?mod=logging&action=login&mobile=2";
        mWebView.loadUrl(loginUrl);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(Constants.BASE_URL + "member.php")) {
                    view.loadUrl(url);
                } else if (url.startsWith(Constants.BASE_URL + "?mobile=2")) {// 首页
                    finish();
                } else if (url.startsWith(Constants.BASE_URL + "member.php?mod=logging&action=logout")) {// 登出
                    view.loadUrl(loginUrl);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String string = CookieManager.getInstance().getCookie(Constants.BASE_URL);

                // PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
                // cookieStore.clear();
                // String[] keyValueSets = CookieManager.getInstance().getCookie(Constants.BASE_URL).split(";");
                // for(String cookie : keyValueSets)
                // {
                // String[] keyValue = cookie.split("=");
                // String key = keyValue[0];
                // String value = "";
                // if(keyValue.length>1) value = keyValue[1];
                // BasicClientCookie2 cookieForRequest = (BasicClientCookie2) new BasicClientCookie2(key, value);
                // cookieForRequest.setDomain(Constants.BASE_URL);
                //
                // cookieForRequest.setPath("/");
                // cookieStore.addCookie(cookieForRequest);
                // }

            }
        });
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

}
