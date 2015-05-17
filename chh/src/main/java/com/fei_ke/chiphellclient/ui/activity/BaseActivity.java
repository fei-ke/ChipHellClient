
package com.fei_ke.chiphellclient.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.fei_ke.chiphellclient.R;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Activity基类
 *
 * @author fei-ke
 * @2014-6-14
 */
@EActivity
public abstract class BaseActivity extends AppCompatActivity {
    protected MenuItem menuItemRefresh;
    boolean mIsRefreshing = true;

    /**
     * 切勿调用和复写此方法
     */
    @AfterViews
    final protected void onPrivateAfterViews() {
        onAfterViews();
        initActionBar(this);
    }

    /**
     * 此方法在onCreate之后调用,勿在此方法上添加@AfterViews注解
     */
    protected abstract void onAfterViews();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSwipeBackLayout().setEdgeTrackingEnabled(GlobalSetting.getSwipeBackEdge());
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItemRefresh = menu.findItem(R.id.action_refresh);
        if (menuItemRefresh != null && mIsRefreshing) {
            menuItemRefresh.setActionView(R.layout.indeterminate_progress_action);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 开始刷新
     */
    public void onStartRefresh() {
        mIsRefreshing = true;

        if (menuItemRefresh != null) {
            menuItemRefresh.setActionView(R.layout.indeterminate_progress_action);
        }
    }

    /**
     * 刷新结束
     */
    public void onEndRefresh() {
        mIsRefreshing = false;

        if (menuItemRefresh != null) {
            menuItemRefresh.setActionView(null);
            menuItemRefresh.setIcon(R.drawable.white_ptr_rotate);
        }
    }

    public static void initActionBar(AppCompatActivity activity) {
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       /*
        if (Build.VERSION.SDK_INT >= 19) {// 设置状态栏
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setNavigationBarAlpha(0);
            if (activity instanceof AlbumActivity) {
                tintManager.setStatusBarTintColor(0);
            } else {
                tintManager.setStatusBarTintResource(R.color.chh_red);
                FrameLayout contentFrameLayout = (FrameLayout) activity.findViewById(android.R.id.content);
                contentFrameLayout.setClipToPadding(false);
                SystemBarTintManager.SystemBarConfig systemBarConfig = new SystemBarTintManager(activity).getConfig();
                contentFrameLayout.setPadding(0, systemBarConfig.getPixelInsetTop(true), systemBarConfig.getPixelInsetRight(),
                        systemBarConfig.getPixelInsetBottom());
            }
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
