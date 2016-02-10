package com.fei_ke.chiphellclient.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.utils.LogMessage;

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

    @ViewById
    SwipeRefreshLayout refreshLayout;

    public static Intent getStartIntent(Context context) {
        return LoginActivity_.intent(context).get();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onAfterViews() {
        setTitle("登录");

        mWebView.getSettings().setJavaScriptEnabled(true);
        final String loginUrl = Constants.BASE_URL + "member.php?mod=logging&action=login&mobile=2";

        refreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.gplus_colors));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        mWebView.loadUrl(loginUrl);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogMessage.i("LoginWebView", url);
                if (url.startsWith(Constants.BASE_URL + "member.php")) {
                    view.loadUrl(url);
                } else if (url.startsWith(Constants.BASE_URL + "member.php?mod=logging&action=logout")) {// 登出
                    view.loadUrl(loginUrl);
                } else if (url.startsWith(Constants.BASE_URL + "?mobile=2")) {// 首页
                    finish();
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                postStartRefresh();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                postEndRefresh();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            mWebView.reload();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * 开始刷新
     */
    public void onStartRefresh() {
        refreshLayout.setRefreshing(true);
    }

    /**
     * 刷新结束
     */
    public void onEndRefresh() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

}
