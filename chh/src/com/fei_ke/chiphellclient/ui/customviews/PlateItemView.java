
package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.Plate;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * 版块item
 * 
 * @author fei-ke
 * @2014-6-15
 */
@EViewGroup(R.layout.layout_plate_item)
public class PlateItemView extends FrameLayout {
    @ViewById(R.id.textView_title)
    TextView textViewTitle;

    @ViewById(R.id.textView_count)
    TextView textViewCount;

    public static PlateItemView getInstance(Context context) {
        return PlateItemView_.build(context);
    }

    public PlateItemView(Context context) {
        super(context);
    }

    void initViews() {

    }

    public void bindValue(Plate plate) {
        textViewTitle.setText(plate.getTitle());
        textViewCount.setText(plate.getXg1());
    }
}
