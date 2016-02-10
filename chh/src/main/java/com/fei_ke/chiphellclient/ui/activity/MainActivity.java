
package com.fei_ke.chiphellclient.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.ui.fragment.PlateListFragment;
import com.fei_ke.chiphellclient.ui.fragment.PlateListFragment.OnPlateClickListener;
import com.fei_ke.chiphellclient.ui.fragment.ThreadListFragment;
import com.fei_ke.chiphellclient.utils.ThemeUtil;
import com.fei_ke.chiphellclient.utils.ToastUtil;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import org.afinal.simplecache.ACache;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

/**
 * 主界面
 *
 * @author fei-ke
 * @2014-6-15
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    public static final int REQUEST_CODE_LOGIN = 0x1;

    private static final String KEY_CACHE_PLATE = "key_cache_plate";

    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    @FragmentById(R.id.fragment_plate_list)
    PlateListFragment mPlateListFragment;

    ThreadListFragment mThreadListFragment;

    @InstanceState
    Plate mPlate;

    @Override
    protected void onAfterViews() {
        ThemeUtil.brandGlowEffect(this);

        // 不允许滑动返回
        getSwipeBackLayout().setEnableGesture(false);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mPlateListFragment.setOnPlateClickListener(new OnPlateClickListener() {

            @Override
            public void onPlateClick(Plate plate) {
                replaceContent(plate);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, drawer, getToolbar(), R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        Plate plate = mPlate;
        mPlate = null;
        if (plate == null) {
            ACache aCache = ACache.get(this);
            plate = (Plate) aCache.getAsObject(KEY_CACHE_PLATE);
        }

        if (plate != null) {
            replaceContent(plate);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        umengUpdate(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlate != null) {
            ACache aCache = ACache.get(this);
            aCache.put(KEY_CACHE_PLATE, mPlate);
        }
    }

    public void replaceContent(Plate plate) {
        if (mPlate == plate) {
            mDrawerLayout.closeDrawers();
            return;
        }
        mPlate = plate;
        mThreadListFragment = getThreadListFragment(plate);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mThreadListFragment, plate.getFid())
                .addToBackStack(mPlate.getFid())
                .commit();
        mDrawerLayout.closeDrawers();
        setTitle(plate.getTitle());
    }

    private ThreadListFragment getThreadListFragment(Plate plate) {
        ThreadListFragment fragment = (ThreadListFragment) getSupportFragmentManager().findFragmentByTag(plate.getFid());
        if (fragment == null) {
            fragment = ThreadListFragment.getInstance(plate);
        }
        return fragment;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.action_test:
                test();
                break;
            case R.id.action_open_source_notices:
                startActivity(new Intent(this, SoftwareNoticesActivity.class));
                break;
            case R.id.action_about:
                startActivity(AboutActivity.getStartIntent(this));
                break;
            case R.id.action_version_update:
                umengUpdate(false);
                break;
            case R.id.action_exit:
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void umengUpdate(final boolean auto) {
        if (!auto) {
            ToastUtil.show(getApplicationContext(), "正在检查新版本");
        }
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
                        startActivity(UpdateActivity.getStartIntent(getApplicationContext(), updateInfo));
                        break;
                    case UpdateStatus.No: // has no update
                        if (!auto) {
                            ToastUtil.show(getApplicationContext(), "没有新版本");
                        }
                        break;
                    case UpdateStatus.NoneWifi: // none wifi
                        break;
                    case UpdateStatus.Timeout: // time out
                        if (!auto) {
                            ToastUtil.show(getApplicationContext(), "网络超时");
                        }
                        break;
                }
            }
        });
        if (auto) {
            UmengUpdateAgent.update(this);
        } else {
            UmengUpdateAgent.forceUpdate(this);
        }
    }

    protected void refresh() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mPlateListFragment.onRefresh();
        } else {
            if (mThreadListFragment != null) {
                mThreadListFragment.onRefresh();
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(resultCode, requestCode, data);
        if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK) {
            refresh();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    void test() {

    }
}
