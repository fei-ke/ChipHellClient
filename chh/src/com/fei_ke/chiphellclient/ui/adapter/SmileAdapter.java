
package com.fei_ke.chiphellclient.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 表情列表适配器
 * 
 * @author fei-ke
 * @2014-6-21
 */
public class SmileAdapter extends BaseAdapter {
    List<Entry<String, String>> mDatas;

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Entry<String, String> getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Entry<String, String> item = getItem(position);
        GifImageView gifImageView = new GifImageView(parent.getContext());
        LayoutParams params = new AbsListView.LayoutParams(120, 120);
        gifImageView.setLayoutParams(params);
        GifDrawable gifDrawable;
        try {
            gifDrawable = new GifDrawable(parent.getContext().getAssets(), item.getValue());
            gifImageView.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gifImageView;
    }

    public void update(List<Entry<String, String>> list) {
        if (mDatas == null) {
            mDatas = new ArrayList<Map.Entry<String, String>>();
        }
        mDatas.addAll(list);

        notifyDataSetChanged();
    }
}
