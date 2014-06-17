
package com.fei_ke.chiphellclient.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.fei_ke.chiphellclient.ChhAplication;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.HtmlParse;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.ui.activity.MainActivity;
import com.fei_ke.chiphellclient.ui.activity.ThreadDetailActivity;
import com.fei_ke.chiphellclient.ui.adapter.ThreadListAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;
import org.apache.http.client.CookieStore;

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
    @AfterViews
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
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Cookie", ChhAplication.getInstance().getCookie());
        // CookieStore cookieStore = new PersistentCookieStore(getActivity());
        // client.setCookieStore(cookieStore);
        RequestParams param = new RequestParams("page", String.valueOf(page));
        client.get(mPlate.getUrl(), param, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                mMainActivity.onStartRefresh();
                System.out.println("onStart(): " + page);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                List<Thread> threads = HtmlParse.parseThreadList(responseBody);
                if (page == 1) {
                    mThreadListAdapter.clear();
                }
                mThreadListAdapter.update(threads);
                // for (Thread thread : threads) {
                // System.out.println(thread);
                // }
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
