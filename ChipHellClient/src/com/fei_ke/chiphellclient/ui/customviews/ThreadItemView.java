
package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.Thread;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

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

    public static ThreadItemView getInstance(Context context) {
        return ThreadItemView_.build(context);
    }

    public ThreadItemView(Context context) {
        super(context);
    }

    public void bindValue(Thread thread) {
        textViewTitle.setText(thread.getTitle());
        textViewCount.setText(thread.getCount());
        textViewBy.setText(thread.getBy());
        textViewDate.setText(thread.getDate());
    }

}
