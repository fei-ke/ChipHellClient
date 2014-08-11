package com.fei_ke.chiphellclient.ui.fragment;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.fei_ke.chiphellclient.ChhApplication;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.PlateClass;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.bean.ThreadListWrap;
import com.fei_ke.chiphellclient.event.FavoriteChangeEvent;
import com.fei_ke.chiphellclient.ui.activity.LoginActivity;
import com.fei_ke.chiphellclient.ui.activity.MainActivity;
import com.fei_ke.chiphellclient.ui.activity.ThreadDetailActivity;
import com.fei_ke.chiphellclient.ui.adapter.ThreadListAdapter;
import com.fei_ke.chiphellclient.ui.customviews.PlateHead;
import com.fei_ke.chiphellclient.utils.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 帖子列表
 *
 * @author fei-ke
 * @2014-6-14
 */
@EFragment(R.layout.fragment_thread_list)
public class ThreadListFragment extends BaseContentFragment implements OnClickListener, OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final int REQUEST_CODE_LOGIN = 100;
    @ViewById(R.id.listView_threads)
    protected PullToRefreshListView mListViewThreads;
    ThreadListAdapter mThreadListAdapter;

    @ViewById
    protected View emptyView;

    @ViewById
    protected TextView textViewError;

    @ViewById(R.id.plateHead)
    protected PlateHead mPlateHeadView;

    @ViewById(R.id.layout_fast_reply)
    protected View layoutFastReply;

    @FragmentArg
    protected Plate mPlate;

    private FastReplyFragment mFastReplyFragment;

    private List<PlateClass> mPlateClasses;

    private int mPage = 1;

    private String url;
    private boolean orderByDate = false;
    // 存储子版块列表
    private List<Plate> platesHold;
    private MainActivity mMainActivity;
    private boolean mIsFreshing;

    /**
     * 获取实例
     *
     * @param plate
     * @return
     */
    public static ThreadListFragment getInstance(Plate plate) {
        return ThreadListFragment_.builder().mPlate(plate).build();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (MainActivity) activity;
    }

    @Override
    protected void onAfterViews() {
        if (mFastReplyFragment == null) {
            mFastReplyFragment = FastReplyFragment.getInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.layout_fast_reply, mFastReplyFragment).commit();
        }

        if (mThreadListAdapter == null) {
            mThreadListAdapter = new ThreadListAdapter();
        }
        mListViewThreads.setAdapter(mThreadListAdapter);
        mListViewThreads.setEmptyView(emptyView);
        emptyView.setOnClickListener(this);
        mListViewThreads.setOnItemClickListener(this);
        mThreadListAdapter.setOnFastReplylistener(this);
        // mListViewThreads.getRefreshableView().setOnScrollListener(onScrollListener);
        mListViewThreads.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getThreadList();
            }
        });
        mListViewThreads.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                if (!mIsFreshing) {
                    getThreadList(++mPage);
                }
            }
        });

        mListViewThreads.setOnScrollListener(onScrollListener);

        // 设置标题或子版块列表
        handSubPlate(platesHold);

        // 没有数据进行数据刷新
        if (mThreadListAdapter.getCount() == 0) {
            mListViewThreads.setRefreshing();
        }

        //设置头部
        mPlateHeadView.bindValue(mPlate, mPlateClasses);

        mPlateHeadView.setOnClassSelectedListener(new PlateHead.OnClassSelectedListener() {

            @Override
            public void onClassSelected(PlateClass plateClass) {
                url = plateClass.getUrl();
                mListViewThreads.setRefreshing();
            }
        });
        mPlateHeadView.setOnOrderBySelectedListener(new PlateHead.OnOrderBySelectedListener() {
            @Override
            public void onOrderBySelected(int index) {
                orderByDate = index == ORDER_BY_DATE;
                mListViewThreads.setRefreshing();
            }
        });

        mPlateHeadView.setOnBtnFavoriteClickListener(this);

        mListViewThreads.getRefreshableView().setOnItemLongClickListener(this);
    }

    private OnScrollListener onScrollListener = new OnScrollListener() {
        int lastVisibleItem;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem > lastVisibleItem) {// 向上滑动中
                hideFastReplyPanel();
                hideHeadPanel();
            }
            if (firstVisibleItem < lastVisibleItem) {
                showHeadPanel();
            }
            lastVisibleItem = firstVisibleItem;

        }
    };

    private void getThreadList() {
        mPage = 1;
        getThreadList(1);
    }

    private void getThreadList(final int page) {
        if (mIsFreshing) {
            return;
        }
        mIsFreshing = true;
        String orderBy = orderByDate ? "dateline" : null;
        ChhApi api = new ChhApi();
        api.getThreadList(getActivity(), url != null ? url : mPlate.getUrl(), page, orderBy, new ApiCallBack<ThreadListWrap>() {
            @Override
            public void onStart() {
                mMainActivity.onStartRefresh();
            }

            @Override
            public void onCache(ThreadListWrap result) {
                onSuccess(result);
            }

            @Override
            public void onSuccess(ThreadListWrap result) {
                if (page == 1) {
                    mThreadListAdapter.clear();
                    List<Plate> plates = result.getPlates();
                    if (!mPlate.isSubPlate() && plates != null) {// 对子版块不进行设置
                        plates.add(0, mPlate);
                        handSubPlate(plates);
                    }

                    // 设置主题分类
                    List<PlateClass> plateClasses = result.getPlateClasses();
                    if (plateClasses != null) {
                        mPlateClasses = plateClasses;
                    } else {
                        List<PlateClass> list = new ArrayList<PlateClass>();
                        PlateClass plateClass = new PlateClass();
                        plateClass.setTitle("全部");
                        plateClass.setUrl(mPlate.getUrl());
                        list.add(plateClass);
                        mPlateClasses = list;
                    }
                    mPlateHeadView.bindValue(mPlate, mPlateClasses);
                }
                mThreadListAdapter.update(result.getThreads());

                if (result.getError() != null) {
                    textViewError.setText(result.getError());
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                ToastUtil.show(getActivity(), "oops 刷新失败了");
            }

            @Override
            public void onFinish() {
                mIsFreshing = false;
                mListViewThreads.onRefreshComplete();
                mMainActivity.onEndRefresh();
            }

        });

    }

    // 创建子版块列表
    protected void handSubPlate(final List<Plate> plates) {
        if (mPlate.isSubPlate() && plates == null) {
            return;
        }
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar == null || plates == null || plates.size() == 0) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            return;
        }
        // 保存子版块记录
        platesHold = plates;

        actionBar.setDisplayShowTitleEnabled(false);
        SpinnerAdapter adapter = new ArrayAdapter<Plate>(getActivity(), R.layout.main_spinner_item, plates);
        actionBar.setListNavigationCallbacks(adapter, new OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                mMainActivity.replaceContent(plates.get(itemPosition));
                return true;
            }
        });
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    }

    @Override
    public void onRefresh() {
        mListViewThreads.setRefreshing();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Thread thread = mThreadListAdapter.getItem((int) id);
        Intent intent = ThreadDetailActivity.getStartIntent(getActivity(), mPlate, thread);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mThreadListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_count:
                if (!ChhApplication.getInstance().isLogin()) {
                    startActivityForResult(LoginActivity.getStartIntent(getActivity()), REQUEST_CODE_LOGIN);
                    break;
                }
                Thread thread = (Thread) v.getTag();
                layoutFastReply.setVisibility(View.VISIBLE);
                mFastReplyFragment.setPlateAndThread(mPlate, thread);
                break;

            case R.id.emptyView:
                getThreadList();
                break;
            case R.id.btnFavorite:
                handleFavorite();
                break;
            default:
                break;
        }
    }

    private void handleFavorite() {

        //未登录时
        if (!ChhApplication.getInstance().isLogin()) {
            startActivityForResult(LoginActivity.getStartIntent(getActivity()), REQUEST_CODE_LOGIN);
            return;
        }

        final boolean isFavorite = mPlate.isFavorite();
        final ChhApi chhApi = new ChhApi();
        ApiCallBack<String> apiCallBack = new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                ToastUtil.show(getActivity(), result);
                EventBus.getDefault().post(new FavoriteChangeEvent());
            }
        };
        if (isFavorite) {//取消收藏
            chhApi.deleteFavorite(mPlate.getFavoriteId(), ChhApplication.getInstance().getFormHash(), apiCallBack);
        } else {
            chhApi.favorite(mPlate.getFid(), ChhApi.TYPE_FORUM, ChhApplication.getInstance().getFormHash(), apiCallBack);
        }
    }

    private void hideFastReplyPanel() {
        if (layoutFastReply.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_view_anim);
            layoutFastReply.startAnimation(animation);
            layoutFastReply.setVisibility(View.GONE);
            mFastReplyFragment.hide();
        }
    }

    protected void showHeadPanel() {
        if (mPlateHeadView.getVisibility() != View.VISIBLE) {
            mPlateHeadView.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_top);
            mPlateHeadView.startAnimation(animation);
        }
    }

    protected void hideHeadPanel() {
        if (mPlateHeadView.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_top);
            mPlateHeadView.startAnimation(animation);
            mPlateHeadView.setVisibility(View.GONE);
        }
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

    /**
     * 收到收藏状态发生变化事件时
     *
     * @param event
     */
    public void onEventMainThread(final FavoriteChangeEvent event) {
        if (event != null && event.getFavoritePlate() != null) {
            List<Plate> favoritePlate = event.getFavoritePlate();
            int index = favoritePlate.indexOf(mPlate);
            if (index != -1) {
                Plate plate = favoritePlate.get(index);
                this.mPlate.setFavoriteId(plate.getFavoriteId());
                this.mPlateHeadView.setFavorite(plate.isFavorite());
            } else {
                this.mPlate.setFavoriteId(null);
                this.mPlateHeadView.setFavorite(false);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Thread thread = mThreadListAdapter.getItem((int) id);
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        final MenuItem menuItemFavorite = popupMenu.getMenu().add("收藏");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item == menuItemFavorite) {
                    new ChhApi().favorite(thread.getTid(), ChhApi.TYPE_THREAD, ChhApplication.getInstance().getFormHash(), new ApiCallBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            ToastUtil.show(getActivity(), result);
                        }
                    });
                }
                return true;
            }
        });
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGIN && resultCode == Activity.RESULT_OK) {
            EventBus.getDefault().post(new FavoriteChangeEvent());
        }
    }
}
