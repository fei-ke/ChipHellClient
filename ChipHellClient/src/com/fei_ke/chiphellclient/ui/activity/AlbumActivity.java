
package com.fei_ke.chiphellclient.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.AlbumWrap;
import com.fei_ke.chiphellclient.ui.adapter.AlbumAdapter;
import com.fei_ke.chiphellclient.utils.LogMessage;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * 相册
 * 
 * @author fei-ke
 * @2014-6-22
 */
@EActivity(R.layout.activity_album)
public class AlbumActivity extends BaseActivity {
    @Extra
    String mUrl;

    @ViewById(R.id.viewPager)
    ViewPager mViewPager;

    @ViewById(R.id.textView_total)
    TextView textViewTotal;

    @ViewById(R.id.textView_current)
    TextView textViewCurrent;

    AlbumAdapter mAdapter;

    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;

    public static Intent getStartIntent(Context context, String url) {
        return AlbumActivity_.intent(context).mUrl(url).get();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Window window = getWindow();
        // window.addFlags(Window.FEATURE_ACTION_BAR_OVERLAY);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setNavigationBarAlpha(0);
        tintManager.setStatusBarAlpha(0);
        if (Build.VERSION.SDK_INT < 19) {
            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        } else {
            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    protected void onAfterViews() {
        mAdapter = new AlbumAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                textViewCurrent.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        ChhApi api = new ChhApi();
        api.getAlbum(mUrl, new ApiCallBack<AlbumWrap>() {

            @Override
            public void onStart() {
                onStartRefresh();
            }

            @Override
            public void onSuccess(AlbumWrap result) {
                LogMessage.i("AlbumActivity#onAfterViews#getAlbum#onSuccess", result);
                mAdapter.update(result.getUrls());

                mViewPager.setCurrentItem(result.getCurPosition(), false);

                textViewTotal.setText(String.valueOf(result.getUrls().size()));
            }

            @Override
            public void onFailure(Throwable error, String content) {
                error.printStackTrace();
            }

            @Override
            public void onFinish() {
                mProgressBar.setVisibility(View.GONE);
                onEndRefresh();
            }
        });
    }

}
