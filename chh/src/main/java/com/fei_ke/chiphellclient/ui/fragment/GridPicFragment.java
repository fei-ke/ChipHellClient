package com.fei_ke.chiphellclient.ui.fragment;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.ui.commen.AnimateFirstDisplayListener;
import com.fei_ke.chiphellclient.ui.customviews.SquareImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片列表
 * Created by fei-ke on 2016/2/17.
 */
@EFragment(R.layout.fragment_grid_pic)
public class GridPicFragment extends BaseFragment {
    @ViewById(R.id.gridView)
    GridView gridView;
    private GridAdapter mAdapter;

    @Override
    protected void onAfterViews() {
        mAdapter = new GridAdapter();
        gridView.setAdapter(mAdapter);

    }

    public void update(List<String> pics) {
        mAdapter.update(pics);
        mAdapter.notifyDataSetChanged();
    }

    public void setSelection(int position) {
        gridView.setSelection(position);
    }

    public void setOnItemClickListener(@Nullable AdapterView.OnItemClickListener listener) {
        gridView.setOnItemClickListener(listener);
    }

    static class GridAdapter extends BaseAdapter {
        private List<String> pics = new ArrayList<>();
        private AnimateFirstDisplayListener firstDisplayListener = new AnimateFirstDisplayListener();

        public void update(List<String> pics) {
            this.pics.clear();
            this.pics.addAll(pics);
        }

        @Override
        public int getCount() {
            return pics == null ? 0 : pics.size();
        }

        @Override
        public String getItem(int position) {
            return pics.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView != null) {
                imageView = (ImageView) convertView;
            } else {
                imageView = new SquareImageView(parent.getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            ImageLoader.getInstance().displayImage(getItem(position), imageView, firstDisplayListener);
            return imageView;
        }
    }
}
