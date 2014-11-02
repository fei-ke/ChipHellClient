package com.fei_ke.chiphellclient.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fei_ke.chiphellclient.ui.fragment.PostListFragment;
import com.fei_ke.chiphellclient.bean.Thread;

/**
 * Created by fei-ke 2014/11/2.
 */
public class PostPageAdapter extends FragmentPagerAdapter {
    private Thread thread;
    private int size = 1;

    public PostPageAdapter(FragmentManager fm, Thread thread) {
        super(fm);
        this.thread = thread;
    }

    @Override
    public Fragment getItem(int i) {
        return PostListFragment.getInstance(i + 1, thread);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return size;
    }
}
