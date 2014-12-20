
package com.fei_ke.chiphellclient.ui.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.AlbumWrap;
import com.fei_ke.chiphellclient.ui.adapter.AlbumAdapter;
import com.fei_ke.chiphellclient.utils.LogMessage;

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
    private static final String TAG = "AlbumActivity";

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
//        Window window = getWindow();
//        // window.addFlags(Window.FEATURE_ACTION_BAR_OVERLAY);
//        SystemBarTintManager tintManager = new SystemBarTintManager(this);
//        tintManager.setStatusBarTintEnabled(true);
//        tintManager.setNavigationBarTintEnabled(true);
//        tintManager.setNavigationBarAlpha(0);
//        tintManager.setStatusBarAlpha(0);
//        if (Build.VERSION.SDK_INT < 19) {
//            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
//        } else {
//            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
        toggleHideyBar();
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

    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     */
    public void toggleHideyBar() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding: Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN. For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }
}
