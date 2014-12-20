package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.PlateClass;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * 版块顶部视图
 *
 * @author fei-ke
 * @2014年7月5日
 */
@EViewGroup(R.layout.layout_plate_head)
public class PlateHead extends FrameLayout {
    @ViewById(R.id.spinnerClass)
    protected Spinner spinnerClass;

    @ViewById(R.id.spinnerOrderBy)
    protected Spinner spinnerOrderBy;

    @ViewById
    protected View btnFavorite;

    private ArrayAdapter<PlateClass> mAdapter;
    private List<PlateClass> mPlateClasses;
    private OnClassSelectedListener mOnClassSelectedListener;
    private OnOrderBySelectedListener mOnOrderBySelectedListener;


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
        mAdapter = new ArrayAdapter<PlateClass>(getContext(), android.R.layout.simple_spinner_dropdown_item, mPlateClasses);
        spinnerClass.setAdapter(mAdapter);
        spinnerClass.setTag(0);
        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((Integer) spinnerClass.getTag() == position) return;

                if (mOnClassSelectedListener != null) {
                    PlateClass plateClass = mPlateClasses.get(position);
                    mOnClassSelectedListener.onClassSelected(plateClass);

                }
                spinnerClass.setTag(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerOrderBy.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{"默认排序", "新帖排序"}));
        spinnerOrderBy.setTag(0);
        spinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((Integer) spinnerOrderBy.getTag() == position) return;

                if (mOnOrderBySelectedListener != null) {
                    mOnOrderBySelectedListener.onOrderBySelected(position);
                }

                spinnerOrderBy.setTag(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void bindValue(Plate plate, List<PlateClass> plateClasses) {
        btnFavorite.setSelected(plate.isFavorite());

        if (plateClasses != null) {
            this.mPlateClasses.clear();
            this.mPlateClasses.addAll(plateClasses);
            mAdapter.notifyDataSetChanged();
        }
    }

    public OnOrderBySelectedListener getOnOrderBySelectedListener() {
        return mOnOrderBySelectedListener;
    }

    public void setOnOrderBySelectedListener(OnOrderBySelectedListener onOrderBySelectedListener) {
        this.mOnOrderBySelectedListener = onOrderBySelectedListener;
    }

    public OnClassSelectedListener getOnClassSelectedListener() {
        return mOnClassSelectedListener;
    }

    public void setOnClassSelectedListener(OnClassSelectedListener onClassSelectedListener) {
        this.mOnClassSelectedListener = onClassSelectedListener;
    }


    public void setOnBtnFavoriteClickListener(OnClickListener onBtnFavoriteClickListener) {
        btnFavorite.setOnClickListener(onBtnFavoriteClickListener);
    }

    public void setFavorite(boolean isFavorite) {
        btnFavorite.setSelected(isFavorite);
    }

    public boolean getFavorite() {
        return btnFavorite.isSelected();
    }


    public static interface OnClassSelectedListener {
        void onClassSelected(PlateClass plateClass);
    }

    public static interface OnOrderBySelectedListener {
        /**
         * 默认排序
         */
        public static final int ORDER_BY_DEFAULT = 0;
        /**
         * 新帖排序
         */
        public static final int ORDER_BY_DATE = 1;

        void onOrderBySelected(int index);
    }

}
