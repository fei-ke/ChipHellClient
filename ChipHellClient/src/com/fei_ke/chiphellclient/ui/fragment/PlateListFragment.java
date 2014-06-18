
package com.fei_ke.chiphellclient.ui.fragment;

import android.app.Activity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.PlateGroup;
import com.fei_ke.chiphellclient.bean.User;
import com.fei_ke.chiphellclient.ui.activity.MainActivity;
import com.fei_ke.chiphellclient.ui.adapter.PlateListAdapter;
import com.fei_ke.chiphellclient.ui.customviews.UserView;
import com.fei_ke.chiphellclient.utils.DensityUtil;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * 版块列表
 * 
 * @author 杨金阳
 * @2014-6-14
 */
@EFragment(R.layout.fragment_plate_list)
public class PlateListFragment extends BaseContentFragment {

    @ViewById(R.id.expandableList_plates)
    FloatingGroupExpandableListView mExpandableListView;
    
    PlateListAdapter mPlateListAdapter;
    List<PlateGroup> mPlateGroups = new ArrayList<PlateGroup>();
    OnPlateClickListener onPlateClickListener;

    MainActivity mMainActivity;

    public static PlateListFragment getInstance() {
        return PlateListFragment_.builder().build();
    }

    public OnPlateClickListener getOnPlateClickListener() {
        return onPlateClickListener;
    }

    public void setOnPlateClickListener(OnPlateClickListener onPlateClickListener) {
        this.onPlateClickListener = onPlateClickListener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (MainActivity) activity;
    }

    @Override
    protected void onAfterViews() {

        final UserView userView = UserView.getInstance(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(DensityUtil.dip2px(getActivity(), 240),
                AbsListView.LayoutParams.WRAP_CONTENT);

        ChhApi api = new ChhApi();
        api.getUserInfo(new ApiCallBack<User>() {
            @Override
            public void onSuccess(User result) {
                userView.bindValue(result);
            }
        });

        mExpandableListView.addHeaderView(userView);

        mPlateListAdapter = new PlateListAdapter(mPlateGroups);
        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(mPlateListAdapter);
        mExpandableListView.setAdapter(wrapperAdapter);

        mExpandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Plate plate = mPlateListAdapter.getChild(groupPosition, childPosition);
                if (onPlateClickListener != null) {
                    onPlateClickListener.onPlateClick(plate);
                    return true;
                }
                return false;
            }
        });

        update();

    }

    public void update() {
        getPlateGroups();
    }

    private void getPlateGroups() {

        ChhApi api = new ChhApi();
        api.getPlateGroups(new ApiCallBack<List<PlateGroup>>() {
            @Override
            public void onSuccess(List<PlateGroup> result) {
                mPlateListAdapter.updateDatas(result);
                for (int i = 0; i < mPlateListAdapter.getGroupCount(); i++) {
                    mExpandableListView.expandGroup(i);
                }
            }

            @Override
            public void onStart() {
                mMainActivity.onStartRefresh();
            }

            @Override
            public void onFinish() {
                mMainActivity.onEndRefresh();
            }
        });

    }

    public static interface OnPlateClickListener {
        void onPlateClick(Plate plate);
    }

    @Override
    public void onRefresh() {
        update();
    }
}
