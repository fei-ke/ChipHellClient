
package com.fei_ke.chiphellclient.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.ui.activity.MainActivity;
import com.fei_ke.chiphellclient.ui.activity.ThreadDetailActivity;
import com.fei_ke.chiphellclient.ui.adapter.ThreadListAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * 帖子列表
 * 
 * @author 杨金阳
 * @2014-6-14
 */
@EFragment(R.layout.fragment_thread_list)
public class ThreadListFragment extends BaseContentFragment implements OnItemClickListener {
    @ViewById(R.id.listView_threads)
    PullToRefreshListView mListViewThreads;
    ThreadListAdapter mThreadListAdapter;

    @FragmentArg
    Plate mPlate;

    int mPage = 1;

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
        mThreadListAdapter = new ThreadListAdapter();
        mListViewThreads.setAdapter(mThreadListAdapter);
        mListViewThreads.setOnItemClickListener(this);
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
        mListViewThreads.setRefreshing();
    }

    private void getThreadList() {
        mPage = 1;
        getThreadList(1);
    }

    private void getThreadList(final int page) {
        mIsFreshing = true;

        ChhApi api = new ChhApi();
        api.getThreadList(mPlate, page, new ApiCallBack<List<Thread>>() {
            @Override
            public void onStart() {
                mMainActivity.onStartRefresh();
                System.out.println("onStart(): " + page);
            }

            @Override
            public void onSuccess(List<Thread> result) {
                if (page == 1) {
                    mThreadListAdapter.clear();
                }
                mThreadListAdapter.update(result);
            }

            @Override
            public void onFinish() {
                mMainActivity.onEndRefresh();
                mIsFreshing = false;
                mListViewThreads.onRefreshComplete();
            }

        });

    }

    @Override
    public void onRefresh() {
        mListViewThreads.setRefreshing();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Thread thread = mThreadListAdapter.getItem((int) id);
        Intent intent = ThreadDetailActivity.getStartIntent(getActivity(), thread);
        startActivity(intent);
    }
}
