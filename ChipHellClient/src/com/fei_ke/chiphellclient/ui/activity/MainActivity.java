
package com.fei_ke.chiphellclient.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.ui.fragment.PlateListFragment;
import com.fei_ke.chiphellclient.ui.fragment.PlateListFragment.OnPlateClickListener;
import com.fei_ke.chiphellclient.ui.fragment.ThreadListFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

/**
 * 主界面
 * 
 * @author 杨金阳
 * @2014-6-15
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    PlateListFragment mPlateListFragment;

    ThreadListFragment mThreadListFragment;

    private MenuItem menuItemRefresh;

    @InstanceState
    Plate mPlate;

    @Override
    @AfterViews
    protected void onAfterViews() {
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        mPlateListFragment = PlateListFragment.getInstance();
        mPlateListFragment.setOnPlateClickListener(new OnPlateClickListener() {

            @Override
            public void onPlateClick(Plate plate) {
                mPlate = plate;
                replaceContent(plate);
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.left_frame, mPlateListFragment)
                .commit();

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
                )
                {
                    public void onDrawerClosed(View view) {
                        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                    }

                    public void onDrawerOpened(View drawerView) {
                        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                    }
                };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (mPlate != null) {
            replaceContent(mPlate);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

    }

    private void replaceContent(Plate plate) {
        mThreadListFragment = ThreadListFragment.getInstance(plate);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mThreadListFragment)
                .commit();
        mDrawerLayout.closeDrawers();
        setTitle(plate.getTitle());
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
        menuItemRefresh = menu.findItem(R.id.action_refresh);
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
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mPlateListFragment.onRefresh();
                } else {
                    if (mThreadListFragment != null) {
                        mThreadListFragment.onRefresh();
                    } else {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
                return true;
            case R.id.action_settings:
                Intent intent = LoginActivity.getStartIntent(this);
                startActivity(intent);
                break;
            case R.id.action_test:
                test();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 开始刷新
     */
    public void onStartRefresh() {
        if (menuItemRefresh != null) {
            menuItemRefresh.setActionView(R.layout.indeterminate_progress_action);
        }
    }

    /**
     * 刷新结束
     */
    public void onEndRefresh() {
        if (menuItemRefresh != null) {
            menuItemRefresh.setActionView(null);
            menuItemRefresh.setIcon(R.drawable.white_ptr_rotate);
        }
    }

    void test() {
        ChhApi api = new ChhApi();
        api.reply("201", "1058176", "d78e4ada", "最后一次。 ", new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                error.printStackTrace();
            }
        });

    }
}
