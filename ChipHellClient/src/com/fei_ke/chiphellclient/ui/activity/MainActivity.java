
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

import com.fei_ke.chiphellclient.ChhAplication;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.ui.fragment.PlateListFragment;
import com.fei_ke.chiphellclient.ui.fragment.PlateListFragment.OnPlateClickListener;
import com.fei_ke.chiphellclient.ui.fragment.ThreadListFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

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

    ThreadListFragment mPostsListFragment;

    private MenuItem menuItemRefresh;

    @Override
    @AfterViews
    protected void onAfterViews() {
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mPlateListFragment = PlateListFragment.getInstance();
        mPlateListFragment.setOnPlateClickListener(new OnPlateClickListener() {

            @Override
            public void onPlateClick(Plate plate) {
                mPostsListFragment = ThreadListFragment.getInstance(plate);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, mPostsListFragment)
                        .commit();
                mDrawerLayout.closeDrawers();
                setTitle(plate.getTitle());
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
                    if (mPostsListFragment != null) {
                        mPostsListFragment.onRefresh();
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
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Cookie", ChhAplication.getInstance().getCookie());
        String url = Constants.BASE_URL + "forum.php?mod=post&action=reply&fid=201&tid=1058176&replysubmit=yes&mobile=yes";
        RequestParams param = new RequestParams();
        param.add("message", "您当前的访问请求当中含有非法字符，已经被系统拒绝");
        param.add("replysubmit", "回复");
        param.add("formhash", "ccb13184");
        client.post(url, param, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                System.out.println(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                
            }
        });

    }
}
