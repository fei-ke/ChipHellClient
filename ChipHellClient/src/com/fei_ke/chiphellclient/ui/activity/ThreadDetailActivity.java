
package com.fei_ke.chiphellclient.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.ui.adapter.PostListAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

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
                    getPostList(++mPage);
                }
            }
        });

        getThreadList();
    }

    private void getThreadList() {
        mPage = 1;
        getPostList(1);
    }

    private void getPostList(final int page) {
        mIsFreshing = true;

        ChhApi api = new ChhApi();
        api.getPostList(mThread, page, new ApiCallBack<List<Post>>() {
            @Override
            public void onStart() {
                System.out.println("onStart(): " + page);
            }

            @Override
            public void onSuccess(List<Post> result) {
                if (page == 1) {
                    mPostListAdapter.clear();
                }
                mPostListAdapter.update(result);
            }

            @Override
            public void onFinish() {
                mIsFreshing = false;
                mRefreshListView.onRefreshComplete();
            }

        });

    }

}
