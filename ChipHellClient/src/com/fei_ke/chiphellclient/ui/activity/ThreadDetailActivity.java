
package com.fei_ke.chiphellclient.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.bean.PrepareQuoteReply;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.ui.adapter.PostListAdapter;
import com.fei_ke.chiphellclient.ui.customviews.PostMainView;
import com.fei_ke.chiphellclient.ui.fragment.FastReplyFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * 帖子内容
 * 
 * @author 杨金阳
 * @2014-6-15
 */
@EActivity(R.layout.activity_thread_detail)
public class ThreadDetailActivity extends BaseActivity implements OnItemLongClickListener {
    @ViewById(R.id.listView_post)
    PullToRefreshListView mRefreshListView;

    PostListAdapter mPostListAdapter;

    @Extra
    Plate mPlate;

    @Extra
    Thread mThread;

    @FragmentByTag("fast_reply")
    FastReplyFragment mFastReplyFragment;

    @ViewById(R.id.main_post)
    PostMainView mMainPostView;

    @ViewById(R.id.layout_fast_reply)
    View mlayoutFastReply;

    int mPage = 1;
    private boolean mIsFreshing;

    public static Intent getStartIntent(Context context, Plate plate, Thread thread) {
        return ThreadDetailActivity_.intent(context).mThread(thread).mPlate(plate).get();
    }

    @Override
    protected void onAfterViews() {
        // mRefreshListView.setMode(Mode.DISABLED);
        mFastReplyFragment.setPlateAndThread(mPlate, mThread);

        mPostListAdapter = new PostListAdapter();
        mRefreshListView.setAdapter(mPostListAdapter);
        setTitle(mThread.getTitle());

        // mRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
        //
        // @Override
        // public void onLastItemVisible() {
        // if (!mIsFreshing) {
        // getPostList(++mPage);
        // }
        // }
        // });
        mRefreshListView.setMode(Mode.BOTH);
        mRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            // 下拉刷新，刷新状态在顶部
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!mIsFreshing) {
                    getPostList();
                }
            }

            // 滑到底部刷新，刷新状态在底部
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!mIsFreshing) {
                    getPostList(++mPage);
                }
            }
        });

        mRefreshListView.getRefreshableView().setOnItemLongClickListener(this);
        mRefreshListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        getPostList();
    }

    private void getPostList() {
        mPage = 1;
        getPostList(1);
    }

    private void getPostList(final int page) {
        mIsFreshing = true;
        if (mPage < 1) {
            mPage = 1;
        }
        ChhApi api = new ChhApi();
        api.getPostList(mThread, page, new ApiCallBack<List<Post>>() {
            @Override
            public void onStart() {
                System.out.println("onStart(): " + page);
            }

            @Override
            public void onSuccess(List<Post> result) {
                if (page == 1) {
                    mMainPostView.bindValue(mThread.getTitle(), result.get(0));
                    mPostListAdapter.clear();
                }
                boolean hasNewData = mPostListAdapter.update(result);
                if (!hasNewData) {
                    mPage -= 2;
                }
            }

            @Override
            public void onFinish() {
                mIsFreshing = false;
                mRefreshListView.onRefreshComplete();
            }

        });

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // 第一条为主贴，无法引用
        if (id == 0) {
            mFastReplyFragment.setPlateAndThread(mPlate, mThread);
            return true;
        }
        Post post = mPostListAdapter.getItem((int) id);
        ChhApi api = new ChhApi();
        api.prepareQuoteReply(post.getReplyUrl(), new ApiCallBack<PrepareQuoteReply>() {
            ProgressDialog dialog;

            @Override
            public void onStart() {
                dialog = new ProgressDialog(ThreadDetailActivity.this);
                dialog.setMessage("正在准备");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }

            @Override
            public void onSuccess(PrepareQuoteReply result) {
                System.out.println(result);
                mFastReplyFragment.setPrepareQuoteReply(result);
            }

            @Override
            public void onFinish() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                error.printStackTrace();
            }
        });
        return true;
    }

}
