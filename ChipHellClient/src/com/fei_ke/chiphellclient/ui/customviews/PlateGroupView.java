
package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.PlateGroup;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * 版块列表分组
 * 
 * @author fei-ke
 * @2014-6-15
 */
@EViewGroup(R.layout.layout_plate_group)
public class PlateGroupView extends FrameLayout {
    @ViewById(R.id.textView_title)
    TextView textViewTitle;

    public static PlateGroupView getInstance(Context context) {
        return PlateGroupView_.build(context);
    }

    public PlateGroupView(Context context) {
        super(context);
    }

    void initViews() {

    }

    public void bindValue(PlateGroup plateGroup) {
        textViewTitle.setText(plateGroup.getTitle());
    }
}
