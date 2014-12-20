
package com.fei_ke.chiphellclient.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnType;
import com.etiennelawlor.quickreturn.library.listeners.SpeedyQuickReturnListViewOnScrollListener;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.bean.PostListWrap;
import com.fei_ke.chiphellclient.bean.PrepareQuoteReply;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.ui.activity.ThreadDetailActivity;
import com.fei_ke.chiphellclient.ui.adapter.PostListAdapter;
import com.fei_ke.chiphellclient.ui.customviews.ExtendListView;
import com.fei_ke.chiphellclient.utils.ToastUtil;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * 回复列表
 *
 * @author fei-ke
 * @2014-6-22
 */
@EFragment(R.layout.fragment_post_list)
public class PostListFragment extends BaseFragment implements AdapterView.OnItemLongClickListener {
    @ViewById(R.id.listView_post)
    ExtendListView mRefreshListView;

    PostListAdapter mPostListAdapter;

    @FragmentArg
    protected int mPage;//回帖页码，从1开始

    @FragmentArg
    protected com.fei_ke.chiphellclient.bean.Thread mThread;

    @InstanceState
    protected ArrayList<Post> mData;

    private boolean mIsFreshing;

    public static PostListFragment getInstance(int page, Thread thread) {
        return PostListFragment_.builder()
                .mPage(page)
                .mThread(thread)
                .build();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostListAdapter = new PostListAdapter();

    }

    @Override
    protected void onAfterViews() {
        mRefreshListView.setAdapter(mPostListAdapter);
        mRefreshListView.setOnItemLongClickListener(this);
        mRefreshListView.setOnScrollListener(
                new SpeedyQuickReturnListViewOnScrollListener(getActivity(),
                        QuickReturnType.FOOTER, null, getQuickReturnView()));

        mRefreshListView.setOnLastItemVisibleListener(new ExtendListView.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
//                showReplyPanel();
            }
        });

        if (mData == null)
            getPostList(mPage);
    }


    private void showReplyPanel() {
        Activity activity = getActivity();
        if (activity instanceof ThreadDetailActivity) {
            ((ThreadDetailActivity) activity).showReplyPanel();
        }
    }

    private void hideReplyPanel() {
        Activity activity = getActivity();
        if (activity instanceof ThreadDetailActivity) {
            ((ThreadDetailActivity) activity).hideReplyPanel();
        }
    }


    private View getQuickReturnView() {
        Activity activity = getActivity();
        if (activity instanceof ThreadDetailActivity) {
            return ((ThreadDetailActivity) activity).getReplyPanel();
        }
        return null;
    }

    private void onEndRefresh() {
        Activity activity = getActivity();
        if (activity instanceof ThreadDetailActivity) {
            ((ThreadDetailActivity) activity).onEndRefresh();
        }
    }

    private void onStartRefresh() {
        Activity activity = getActivity();
        if (activity instanceof ThreadDetailActivity) {
            ((ThreadDetailActivity) activity).onStartRefresh();
        }
    }

    private void setPrepareQuoteReply(PrepareQuoteReply quoteReply) {
        Activity activity = getActivity();
        if (activity instanceof ThreadDetailActivity) {
            ((ThreadDetailActivity) activity).setPrepareQuoteReply(quoteReply);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO 第一条为主贴，无法引用
        if (mPage == 1 && id == 0) {
//            mFastReplyFragment.setPlateAndThread(mPlate, mThread);
            return true;
        }

        Post post = mPostListAdapter.getItem((int) id);

        String url = post.getReplyUrl();
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(getActivity(), R.string.not_login, Toast.LENGTH_SHORT).show();
            return true;
        }
        ChhApi api = new ChhApi();
        api.prepareQuoteReply(post.getReplyUrl(), new ApiCallBack<PrepareQuoteReply>() {
            ProgressDialog dialog;

            @Override
            public void onStart() {
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("正在准备……");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }

            @Override
            public void onSuccess(PrepareQuoteReply result) {
                setPrepareQuoteReply(result);
                if (dialog.isShowing()) {
                    showReplyPanel();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                error.printStackTrace();
                ToastUtil.show(getActivity(), "oops 获取失败了");
            }

            @Override
            public void onFinish() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

        });
        return true;
    }

    public void update(List<Post> posts) {
        if (mPage == 1) {
            posts.remove(0);
        }
        mPostListAdapter.update(posts);
        mRefreshListView.setSelection(mPostListAdapter.getCount() - 1);
    }

    public void update() {
        getPostList(mPage);
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
        api.getPostList(getActivity(), mThread, page, new ApiCallBack<PostListWrap>() {
            @Override
            public void onStart() {
                onStartRefresh();
            }

            @Override
            public void onCache(PostListWrap result) {
                if (result == null || result.size() == 0 || mData != null) {
                    return;
                }

//                if (page == 1) {
//                    loadMainContent(result.get(0));
//                    mPostListAdapter.clear();
//                }
                List<Post> posts = result.getPosts();
                if (mPage == 1) {
                    posts.get(0).setContent("");
                }
                mPostListAdapter.update(posts);
            }

            @Override
            public void onSuccess(PostListWrap result) {
                if (result == null || result.size() == 0) {
                    return;
                }

//                if (page == 1) {
//                    loadMainContent(result.get(0));
//                    mPostListAdapter.clear();
//
//                    // 将该帖子设为已读
//                    new ThreadStatusUtil(getApplicationContext()).setRead(mThread.getTid());
//                }
                mData = (ArrayList<Post>) result.getPosts();
                if (mPage == 1) {
                    mData.get(0).setContent("");
                    mData.get(0).setImgList(null);
                }
                boolean hasNewData = mPostListAdapter.update(mData);
                if (!hasNewData) {
                    // mPage -= 1;
                    // TODO页码处理
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                ToastUtil.show(getActivity(), "oops 刷新失败了");
            }

            @Override
            public void onFinish() {
                mIsFreshing = false;
                onEndRefresh();
            }

        });

    }


    public boolean isListOnTop() {
        if (null == mPostListAdapter || mPostListAdapter.isEmpty()) {
            return true;

        } else {
            /**
             * This check should really just be:
             * mRefreshableView.getFirstVisiblePosition() == 0, but PtRListView
             * internally use a HeaderView which messes the positions up. For
             * now we'll just add one to account for it and rely on the inner
             * condition which checks getTop().
             */
            if (mRefreshListView.getFirstVisiblePosition() == 0) {
                final View firstVisibleChild = mRefreshListView.getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= mRefreshListView.getTop();
                }
            }
        }

        return false;
    }

}
