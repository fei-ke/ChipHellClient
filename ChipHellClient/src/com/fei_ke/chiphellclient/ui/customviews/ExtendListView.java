package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
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

    private int mItemCount;
    private int mItemOffsetY[];
    private boolean scrollIsComputed = false;
    private int mHeight;

    public int getListHeight() {
        return mHeight;
    }

    public void computeScrollY() {
        mHeight = 0;
        mItemCount = getAdapter().getCount();
        if (mItemOffsetY == null) {
            mItemOffsetY = new int[mItemCount];
        }
        for (int i = 0; i < mItemCount; ++i) {
            View view = getAdapter().getView(i, null, this);
            view.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            mItemOffsetY[i] = mHeight;
            mHeight += view.getMeasuredHeight();
            System.out.println("mHeight: " + mHeight);
        }
        scrollIsComputed = true;
    }

    public boolean scrollYIsComputed() {
        return scrollIsComputed;
    }

    public int getComputedScrollY() {
        int pos, nScrollY, nItemY;
        View view = null;
        pos = getFirstVisiblePosition();
        view = getChildAt(0);
        nItemY = view.getTop();
        nScrollY = mItemOffsetY[pos] - nItemY;
        return nScrollY;
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
