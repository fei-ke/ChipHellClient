
package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.PlateClass;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * 版块顶部视图
 *
 * @author 杨金阳
 * @2014年7月5日
 */
@EViewGroup(R.layout.layout_plate_head)
public class PlateHead extends FrameLayout {
    @ViewById(R.id.spinner)
    Spinner spinner;
    ArrayAdapter<PlateClass> mAdapter;
    List<PlateClass> mPlateClasses;
    OnItemSeleckedListener mOnItemSeleckedListener;

    public PlateHead(Context context) {
        super(context);
    }

    public PlateHead(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PlateHead(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    void afterViews() {
        mPlateClasses = new ArrayList<PlateClass>();
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, mPlateClasses);
        spinner.setAdapter(mAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mOnItemSeleckedListener != null) {
                    PlateClass plateClass = mPlateClasses.get(position);
                    mOnItemSeleckedListener.onItemSelecked(plateClass);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void bindValue(List<PlateClass> plateClasses) {
        this.mPlateClasses.clear();
        this.mPlateClasses.addAll(plateClasses);
        mAdapter.notifyDataSetChanged();
    }

    public OnItemSeleckedListener getOnItemSeleckedListener() {
        return mOnItemSeleckedListener;
    }

    public void setOnItemSeleckedListener(OnItemSeleckedListener onItemSeleckedListener) {
        this.mOnItemSeleckedListener = onItemSeleckedListener;
    }

    public static interface OnItemSeleckedListener {
        void onItemSelecked(PlateClass plateClass);
    }

}
