
package com.fei_ke.chiphellclient.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.fei_ke.chiphellclient.ui.activity.LoginActivity;
import com.fei_ke.chiphellclient.ui.activity.MainActivity;
import com.fei_ke.chiphellclient.ui.adapter.PlateListAdapter;
import com.fei_ke.chiphellclient.ui.customviews.UserView;
import com.fei_ke.chiphellclient.utils.LogMessage;
import com.fei_ke.chiphellclient.utils.ToastUtil;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * 版块列表
 *
 * @author fei-ke
 * @2014-6-14
 */
@EFragment(R.layout.fragment_plate_list)
public class PlateListFragment extends BaseContentFragment {

    private static final String TAG = "PlateListFragment";

    @ViewById(R.id.expandableList_plates)
    FloatingGroupExpandableListView mExpandableListView;

    PlateListAdapter mPlateListAdapter;
    List<PlateGroup> mPlateGroups = new ArrayList<PlateGroup>();
    OnPlateClickListener onPlateClickListener;
    UserView mUserView;
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

        mUserView = UserView.newInstance(getActivity());
        mUserView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = LoginActivity.getStartIntent(mMainActivity);
                getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_LOGIN);
            }
        });

        mExpandableListView.addHeaderView(mUserView);

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
        api.getUserInfo(new ApiCallBack<User>() {
            @Override
            public void onSuccess(User result) {
                LogMessage.i(TAG, result);
                mUserView.bindValue(result);
            }
        });

        api.getPlateGroups(getActivity(), new ApiCallBack<List<PlateGroup>>() {
            @Override
            public void onCache(List<PlateGroup> result) {
                onSuccess(result);
            }

            @Override
            public void onSuccess(List<PlateGroup> result) {
                mPlateListAdapter.updateDatas(result);
                for (int i = 0; i < mPlateListAdapter.getGroupCount(); i++) {
                    mExpandableListView.expandGroup(i);
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                ToastUtil.show(getActivity(), "oops 刷新失败了");
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
