
package com.fei_ke.chiphellclient.ui.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.ui.customviews.ThreadItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * 帖子列表适配器
 * 
 * @author 杨金阳
 * @2014-6-15
 */
public class ThreadListAdapter extends BaseAdapter {
    private List<Thread> mThreads;
    private OnClickListener onFastReplylistener;

    @Override
    public int getCount() {
        return mThreads == null ? 0 : mThreads.size();
    }

    @Override
    public Thread getItem(int position) {
        return mThreads.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ThreadItemView threadItemView = null;
        if (convertView == null) {
            threadItemView = ThreadItemView.getInstance(parent.getContext());
        } else {
            threadItemView = (ThreadItemView) convertView;
        }
        Thread thread = getItem(position);
        threadItemView.bindValue(thread);
        if (onFastReplylistener != null) {
            threadItemView.getTextViewCount().setTag(thread);
            threadItemView.setOnFastReplyClickListener(onFastReplylistener);
        }
        return threadItemView;
    }

    public OnClickListener getOnFastReplylistener() {
        return onFastReplylistener;
    }

    public void setOnFastReplylistener(OnClickListener onFastReplylistener) {
        this.onFastReplylistener = onFastReplylistener;
    }

    public void update(List<Thread> threads) {
        if (mThreads == null) {
            mThreads = new ArrayList<Thread>();
        }/*
          * else {
          * mThreads.clear();
          * }
          */
        mThreads.addAll(threads);
        notifyDataSetChanged();
    }

    public void clear() {
        if (mThreads != null) {
            mThreads.clear();
        }
    }

}
