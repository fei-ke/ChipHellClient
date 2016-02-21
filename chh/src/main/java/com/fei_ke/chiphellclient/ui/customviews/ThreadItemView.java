
package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.ui.commen.AnimateFirstDisplayListener;
import com.fei_ke.chiphellclient.utils.ThreadStatusUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * 帖子列表的一个item
 *
 * @author fei-ke
 * @2014-6-16
 */
@EViewGroup(R.layout.layout_thread_item)
public class ThreadItemView extends FrameLayout {
    @ViewById(R.id.textView_title)
    TextView textViewTitle;

    @ViewById(R.id.textView_count)
    TextView textViewCount;

    @ViewById(R.id.textView_by)
    TextView textViewBy;

    @ViewById(R.id.textView_date)
    TextView textViewDate;

    @ViewById(R.id.imageView_icon)
    ImageView imageViewIcon;

    @ViewById(R.id.textView_status)
    View viewStatus;

    ThreadStatusUtil statusUtil;

    private AnimateFirstDisplayListener firstDisplayListener = new AnimateFirstDisplayListener();

    public static ThreadItemView getInstance(Context context) {
        return ThreadItemView_.build(context);
    }

    public ThreadItemView(Context context) {
        super(context);
        statusUtil = new ThreadStatusUtil(getContext());
    }

    public void bindValue(Thread thread) {
        textViewTitle.setText(thread.getTitle());
        textViewCount.setText(thread.getCount());
        textViewBy.setText(thread.getBy());
        textViewDate.setText(thread.getDate());
        String imgSrc = thread.getImgSrc();
        if (TextUtils.isEmpty(imgSrc)) {
            imageViewIcon.setVisibility(GONE);
        } else {
            imageViewIcon.setVisibility(VISIBLE);
            ImageLoader.getInstance().displayImage(imgSrc, imageViewIcon, firstDisplayListener);
        }

        if (thread.getTitleColor() != 0) {
            textViewTitle.setTextColor(thread.getTitleColor());
        } else {
            textViewTitle.setTextColor(Color.BLACK);
        }

        if (statusUtil.isRead(thread.getTid())) {
            viewStatus.setVisibility(View.VISIBLE);
        } else {
            viewStatus.setVisibility(View.GONE);
        }
    }

    public TextView getTextViewCount() {
        return textViewCount;
    }

    public void setOnFastReplyClickListener(OnClickListener listener) {
        textViewCount.setOnClickListener(listener);
    }
}
