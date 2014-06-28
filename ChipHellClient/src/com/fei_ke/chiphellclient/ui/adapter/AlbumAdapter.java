
package com.fei_ke.chiphellclient.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.fei_ke.chiphellclient.ui.fragment.PicFargment;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册适配器
 * 
 * @author fei-ke
 * @2014-6-22
 */
public class AlbumAdapter extends FragmentStatePagerAdapter {
    List<String> mDatas;

    public AlbumAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PicFargment.getInstance(mDatas.get(position));
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public void update(List<String> list) {
        if (mDatas == null) {
            mDatas = new ArrayList<String>();

        }
        mDatas.addAll(list);

        notifyDataSetChanged();
    }
}
