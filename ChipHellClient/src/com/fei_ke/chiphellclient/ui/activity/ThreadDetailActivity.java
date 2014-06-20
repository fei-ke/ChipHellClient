
package com.fei_ke.chiphellclient.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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

    @ViewById(R.id.sliding_layout)
    SlidingUpPanelLayout mPanelLayout;

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
        setTitle(mPlate.getTitle());
        getActionBar().setSubtitle(mThread.getTitle());

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
        mRefreshListView.setOnPullEventListener(new OnPullEventListener<ListView>() {

            @Override
            public void onPullEvent(PullToRefreshBase<ListView> refreshView, State state, Mode direction) {
                if (state.equals(State.PULL_TO_REFRESH) && direction.equals(Mode.PULL_FROM_START)) {
                    mPanelLayout.collapsePanel();
                }
            }
        });
        getPostList();
    }

    private void getPostList() {
        mPage = 1;
        getPostList(1);
    }

    private void getPostList(final int page) {
        if (mIsFreshing) {
            return;
        }
        mIsFreshing = true;
        if (mPage < 1) {
            mPage = 1;
        }
        ChhApi api = new ChhApi();
        api.getPostList(mThread, page, new ApiCallBack<List<Post>>() {
            @Override
            public void onStart() {
                onStartRefresh();
            }

            @Override
            public void onSuccess(List<Post> result) {
                if (result == null || result.size() == 0) {
                    return;
                }

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
                onEndRefresh();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getPostList();
                return true;
            case R.id.action_brower:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mThread.getUrl()));
                startActivity(intent);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.thread_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
