
package com.fei_ke.chiphellclient.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.ui.adapter.AlbumAdapter;
import com.fei_ke.chiphellclient.ui.fragment.GridPicFragment;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

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
    ArrayList<String> pics;
    @Extra
    int index;

    @ViewById(R.id.viewPager)
    ViewPager mViewPager;

    @ViewById(R.id.textView_total)
    TextView textViewTotal;

    @ViewById(R.id.textView_current)
    TextView textViewCurrent;

    @ViewById(R.id.layout_grid)
    View layoutGrid;

    @ViewById(R.id.layout_viewpager)
    View layoutViewpager;

    @FragmentById(R.id.gridPicFragment)
    GridPicFragment gridPicFragment;

    AlbumAdapter mViewPagerAdapter;

    public static Intent getStartIntent(Context context, List<String> pics, int index) {
        return AlbumActivity_.intent(context).pics(new ArrayList<String>(pics)).index(index).get();
    }

    @Override
    protected void onAfterViews() {
        getToolbar().setBackgroundColor(Color.parseColor("#20000000"));

        mViewPagerAdapter = new AlbumAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                textViewCurrent.setText(String.valueOf(position + 1));
            }
        });

        gridPicFragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                layoutGrid.setVisibility(View.GONE);
                layoutViewpager.setVisibility(View.VISIBLE);
                mViewPager.setCurrentItem(position, false);
            }
        });

        initPic(pics, index);
    }

    private void initPic(List<String> pics, int index) {
        mViewPagerAdapter.update(pics);
        mViewPager.setCurrentItem(index);
        textViewTotal.setText(String.valueOf(pics.size()));
        textViewCurrent.setText(String.valueOf(index + 1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_switch) {
            if (layoutGrid.getVisibility() != View.VISIBLE) {
                gridPicFragment.update(pics);
                gridPicFragment.setSelection(mViewPager.getCurrentItem());
                layoutGrid.setVisibility(View.VISIBLE);
                layoutViewpager.setVisibility(View.GONE);
            } else {
                layoutGrid.setVisibility(View.GONE);
                layoutViewpager.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
