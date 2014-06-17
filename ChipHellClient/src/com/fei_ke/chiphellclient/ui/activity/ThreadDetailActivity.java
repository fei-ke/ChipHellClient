
package com.fei_ke.chiphellclient.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.fei_ke.chiphellclient.ChhAplication;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.HtmlParse;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.constant.Post;
import com.fei_ke.chiphellclient.ui.adapter.PostListAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.util.List;

/**
 * 帖子内容
 * 
 * @author 杨金阳
 * @2014-6-15
 */
@EActivity(R.layout.activity_thread_detail)
public class ThreadDetailActivity extends BaseActivity {
    @ViewById(R.id.listView_post)
    PullToRefreshListView mRefreshListView;

    PostListAdapter mPostListAdapter;
    @Extra
    Thread mThread;
    int mPage = 1;
    private boolean mIsFreshing;

    public static Intent getStartIntent(Context context, Thread thread) {
        return ThreadDetailActivity_.intent(context).mThread(thread).get();
    }

    @Override
    protected void onAfterViews() {
        // mRefreshListView.setMode(Mode.DISABLED);
        
        mPostListAdapter = new PostListAdapter();
        mRefreshListView.setAdapter(mPostListAdapter);
        setTitle(mThread.getTitle());
        
        mRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                if (!mIsFreshing) {
                    getThreadList(++mPage);
                }
            }
        });
        
        getThreadList();
    }
    private void getThreadList() {
        mPage = 1;
        getThreadList(1);
    }
    private void getThreadList(final int page) {
        mIsFreshing = true;
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Cookie", ChhAplication.getInstance().getCookie());

        RequestParams param = new RequestParams("page", String.valueOf(page));
        param.add("mobile", "2");
        System.out.println(mThread.getUrl());
        client.get(mThread.getUrl(), param, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                System.out.println("onStart(): " + page);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                List<Post> posts = HtmlParse.parsePostList(responseBody);
                if (page == 1) {
                    mPostListAdapter.clear();
                }
                mPostListAdapter.update(posts);
                // for (Thread thread : threads) {
                // System.out.println(thread);
                // }
            }

            @Override
            public void onFinish() {
                mIsFreshing = false;
                mRefreshListView.onRefreshComplete();
            }
        });
    }

}
