
package com.fei_ke.chiphellclient.ui.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.constant.SmileTable;
import com.fei_ke.chiphellclient.ui.adapter.SmileAdapter;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 表情
 * 
 * @author fei-ke
 * @2014-6-21
 */
@EFragment(R.layout.fragment_smile)
public class SmileFragment extends BaseFragment {

    @ViewById(R.id.gridView)
    GridView mGridView;

    SmileAdapter mAdapter;

    OnSmileChoose mOnSmileChoose;

    public static SmileFragment getInstance() {
        return SmileFragment_.builder().build();
    }

    @Override
    protected void onAfterViews() {
        mAdapter = new SmileAdapter();
        mGridView.setAdapter(mAdapter);
        List<Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>();
        for (Entry<String, String> entry : SmileTable.smilis.entrySet()) {
            list.add(entry);
        }
        mAdapter.update(list);

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnSmileChoose != null) {
                    Entry<String, String> entry = mAdapter.getItem(position);
                    mOnSmileChoose.onSmileChoose(entry);
                }
            }
        });

    }

    public OnSmileChoose getmOnSmileChoose() {
        return mOnSmileChoose;
    }

    public void setmOnSmileChoose(OnSmileChoose mOnSmileChoose) {
        this.mOnSmileChoose = mOnSmileChoose;
    }

    public static interface OnSmileChoose {
        void onSmileChoose(Entry<String, String> smile);
    }
}
