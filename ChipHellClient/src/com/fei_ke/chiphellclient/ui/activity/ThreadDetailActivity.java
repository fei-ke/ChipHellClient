
package com.fei_ke.chiphellclient.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.constant.Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * 帖子内容
 * 
 * @author 杨金阳
 * @2014-6-15
 */
@EActivity(R.layout.activity_thread_detail)
public class ThreadDetailActivity extends BaseActivity {
    @ViewById(R.id.webView)
    WebView mWebView;

    @Extra
    Thread mThread;

    public static Intent getStartIntent(Context context, Thread thread) {
        return ThreadDetailActivity_.intent(context).mThread(thread).get();
    }

    @Override
    @AfterViews
    protected void onAfterViews() {
        mWebView.loadUrl(Constants.BASE_URL + mThread.getUrl());
    }

}
