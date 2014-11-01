package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by fei-ke on 2014/11/1.
 */
public class ExtendListView extends ListView implements AbsListView.OnScrollListener {
    private OnLastItemVisibleListener mOnLastItemVisibleListener;
    private boolean mLastItemVisible;
    private OnScrollListener mOnScrollListener;

    public ExtendListView(Context context) {
        super(context);
        super.setOnScrollListener(this);
    }

    public ExtendListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnScrollListener(this);
    }

    public ExtendListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setOnScrollListener(this);
    }

    public final void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    public final void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
                               final int totalItemCount) {
        /**
         * Set whether the Last Item is Visible. lastVisibleItemIndex is a
         * zero-based index, so we minus one totalItemCount to check
         */
        if (null != mOnLastItemVisibleListener) {
            mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
        }


        // Finally call OnScrollListener if we have one
        if (null != mOnScrollListener) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public final void onScrollStateChanged(final AbsListView view, final int state) {
        /**
         * Check that the scrolling has stopped, and that the last item is
         * visible.
         */
        if (state == OnScrollListener.SCROLL_STATE_IDLE && null != mOnLastItemVisibleListener && mLastItemVisible) {
            mOnLastItemVisibleListener.onLastItemVisible();
        }

        if (null != mOnScrollListener) {
            mOnScrollListener.onScrollStateChanged(view, state);
        }
    }

    public final void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
        mOnLastItemVisibleListener = listener;
    }

    public static interface OnLastItemVisibleListener {
        public void onLastItemVisible();
    }
}
