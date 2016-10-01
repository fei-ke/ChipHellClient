
package com.fei_ke.chiphellclient.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.analytics.Analytics;
import com.fei_ke.chiphellclient.utils.GlobalSetting;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Activity基类
 *
 * @author fei-ke
 * @2014-6-14
 */
@EActivity
public abstract class BaseActivity extends SwipeBackActivity {
    protected MenuItem menuItemRefresh;
    boolean mIsRefreshing = true;
    private Toolbar toolbar;

    /**
     * 切勿调用和复写此方法
     */
    @AfterViews
    final protected void onPrivateAfterViews() {
        onAfterViews();
    }

    /**
     * 此方法在onCreate之后调用,勿在此方法上添加@AfterViews注解
     */
    protected abstract void onAfterViews();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(GlobalSetting.getSwipeBackEdge());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    public void setContentView(int layoutId) {
        setContentView(View.inflate(this, layoutId, null));
    }

    @Override
    public void setContentView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar == null) {
            super.setContentView(R.layout.activity_base);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            FrameLayout rootLayout = (FrameLayout) findViewById(R.id.root_layout);
            if (rootLayout != null) {
                rootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else {
            super.setContentView(view);
        }
        initToolBar(toolbar);

    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    private void initToolBar(@NonNull Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Analytics.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Analytics.onPause(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItemRefresh = menu.findItem(R.id.action_refresh);
        if (menuItemRefresh != null && mIsRefreshing) {
            postStartRefresh();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void postStartRefresh() {
        mIsRefreshing = true;

        if (menuItemRefresh != null) {
            menuItemRefresh.setActionView(R.layout.indeterminate_progress_action);
        }
        onStartRefresh();
    }

    /**
     * 开始刷新
     */
    protected void onStartRefresh() {

    }

    public void postEndRefresh() {
        mIsRefreshing = false;

        if (menuItemRefresh != null) {
            menuItemRefresh.setActionView(null);
            menuItemRefresh.setIcon(R.drawable.ic_renew);
        }
        onEndRefresh();
    }

    /**
     * 刷新结束
     */
    protected void onEndRefresh() {

    }

    public void setSubtitle(CharSequence title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(title);
        }
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
