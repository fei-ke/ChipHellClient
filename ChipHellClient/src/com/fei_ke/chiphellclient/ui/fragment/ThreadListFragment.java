
package com.fei_ke.chiphellclient.ui.fragment;

import android.app.Activity;
import android.widget.ListView;

import com.fei_ke.chiphellclient.ChhAplication;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.HtmlParse;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.ui.activity.MainActivity;
import com.fei_ke.chiphellclient.ui.adapter.ThreadListAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.List;

/**
 * 帖子列表
 * 
 * @author 杨金阳
 * @2014-6-14
 */
@EFragment(R.layout.fragment_posts_list)
public class ThreadListFragment extends BaseContentFragment {
    @ViewById(R.id.listView_threads)
    ListView mListViewThreads;
    ThreadListAdapter mThreadListAdapter;

    @FragmentArg
    Plate mPlate;

    private MainActivity mMainActivity;

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
        getThreadList();
    }

    private void getThreadList() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Cookie", ChhAplication.getInstance().getCookie());

        client.get(Constants.BASE_URL + "/" + mPlate.getUrl(), new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                mMainActivity.onStartRefresh();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                List<Thread> threads = HtmlParse.parsePostsList(responseBody);
                mThreadListAdapter.update(threads);
                for (Thread thread : threads) {
                    System.out.println(thread);
                }
            }

            @Override
            public void onFinish() {
                mMainActivity.onEndRefresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        getThreadList();

    }
}
