package com.fei_ke.chiphellclient.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.fei_ke.chiphellclient.ChhApplication;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.api.support.ApiCallBack;
import com.fei_ke.chiphellclient.api.support.ApiHelper;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.PlateGroup;
import com.fei_ke.chiphellclient.bean.User;
import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.event.FavoriteChangeEvent;
import com.fei_ke.chiphellclient.ui.activity.LoginActivity;
import com.fei_ke.chiphellclient.ui.activity.MainActivity;
import com.fei_ke.chiphellclient.ui.adapter.PlateListAdapter;
import com.fei_ke.chiphellclient.ui.customviews.UserView;
import com.fei_ke.chiphellclient.utils.LogMessage;
import com.fei_ke.chiphellclient.utils.ToastUtil;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

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
    protected ExpandableListView mExpandableListView;
    @ViewById(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;

    private PlateListAdapter mPlateListAdapter;
    private List<PlateGroup> mPlateGroups = new ArrayList<PlateGroup>();
    private OnPlateClickListener onPlateClickListener;
    private UserView mUserView;
    private MainActivity mMainActivity;

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
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.gplus_colors));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
        mUserView = UserView.newInstance(getActivity());
        mUserView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = LoginActivity.getStartIntent(mMainActivity);
                getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_LOGIN);
            }
        });
        //我的主题
        final Plate plateMyPost = new Plate();
        plateMyPost.setFid("my_post");//设置fid用于fragment缓存
        plateMyPost.setTitle("我的主题");
        plateMyPost.setUrl(Constants.BASE_URL + "home.php?mod=space&do=thread&view=me&mobile=1");
        mUserView.getButtonMyPost().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlateClickListener != null) {
                    onPlateClickListener.onPlateClick(plateMyPost);
                }
            }
        });

        //我的收藏
        final Plate plateFavorite = new Plate();
        plateFavorite.setFid("my_favorite");//设置fid用于fragment缓存
        plateFavorite.setTitle("我的收藏");
        plateFavorite.setUrl(Constants.BASE_URL + "home.php?mod=space&do=favorite&view=me&type=thread&mobile=1");
        mUserView.getButtonFavorite().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlateClickListener != null) {
                    onPlateClickListener.onPlateClick(plateFavorite);
                }
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

        //Retrofit retrofit = new Retrofit.Builder()
        //        .baseUrl(Constants.BASE_URL)
        //        .addConverterFactory(new Converter.Factory() {
        //            @Override
        //            public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        //                retrofit.
        //                return super.responseBodyConverter(type, annotations, retrofit);
        //            }
        //        })
        //        .build();
        //API api = retrofit.create(API.class);
        //api.getPlateGroups().enqueue(new Callback<List<PlateGroup>>() {
        //    @Override
        //    public void onResponse(Response<List<PlateGroup>> response) {
        //        response.body();
        //    }
        //
        //    @Override
        //    public void onFailure(Throwable t) {
        //
        //    }
        //});

        ApiHelper.requestApi(ChhApi.getUserInfo(), new ApiCallBack<User>() {
            @Override
            public void onCache(User result) {
                onSuccess(result);
            }

            @Override
            public void onSuccess(User result) {
                LogMessage.i(TAG, result);
                mUserView.bindValue(result);
                ChhApplication.getInstance().setFormHash(result.getFormHash());
            }
        });

        ApiHelper.requestApi(ChhApi.getPlateGroups(), new com.fei_ke.chiphellclient.api.support.ApiCallBack<List<PlateGroup>>() {
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

                setPlateFavoriteStatus(result);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                error.printStackTrace();
                ToastUtil.show(getActivity(), "oops 刷新失败了");
            }

            @Override
            public void onStart() {
                mMainActivity.postStartRefresh();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFinish() {
                mMainActivity.postEndRefresh();
            }
        });

    }

    private void setPlateFavoriteStatus(List<PlateGroup> plateGroups) {
        if (plateGroups != null && plateGroups.size() > 0) {
            updateAllFavoriteStatus(plateGroups);
        }
    }

    @Background
    protected void updateAllFavoriteStatus(List<PlateGroup> plateGroups) {
        EventBus.getDefault().post(new FavoriteChangeEvent(plateGroups.get(0).getPlates()));
    }

    public static interface OnPlateClickListener {
        void onPlateClick(Plate plate);
    }

    @Override
    public void onRefresh() {
        update();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(FavoriteChangeEvent event) {
        if (event.getFavoritePlate() == null) {
            update();
        }
    }
}
