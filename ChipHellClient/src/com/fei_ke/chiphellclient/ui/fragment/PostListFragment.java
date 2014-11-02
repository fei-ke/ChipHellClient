
package com.fei_ke.chiphellclient.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.bean.PostListWrap;
import com.fei_ke.chiphellclient.bean.PrepareQuoteReply;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.ui.activity.ThreadDetailActivity;
import com.fei_ke.chiphellclient.ui.adapter.PostListAdapter;
import com.fei_ke.chiphellclient.utils.ToastUtil;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * 回复列表
 *
 * @author fei-ke
 * @2014-6-22
 */
@EFragment(R.layout.fragment_post_list)
public class PostListFragment extends BaseFragment implements AdapterView.OnItemLongClickListener {
    @ViewById(R.id.listView_post)
    ListView mRefreshListView;

    PostListAdapter mPostListAdapter;

    @FragmentArg
    protected int mPage;

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
        mRefreshListView.setOnScrollListener(onScrollListener);
        if (mData == null)
            getPostList(mPage);
    }

    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        int lastVisibleItem;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem > lastVisibleItem) {// 向下
                hideReplyPanel();
            } else if (firstVisibleItem < lastVisibleItem) {
                showReplyPanel();
            }
            lastVisibleItem = firstVisibleItem;
        }
    };

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
            ((ThreadDetailActivity) activity).onStartRefresh();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO 第一条为主贴，无法引用
        if (id == 0) {
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
                if (result == null || result.size() == 0) {
                    return;
                }

//                if (page == 1) {
//                    loadMainContent(result.get(0));
//                    mPostListAdapter.clear();
//                }
                mPostListAdapter.update(result.getPosts());
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
                //TODO fei-ke 2014/11/1 刷新完成
//                mRefreshListView.onRefreshComplete();
                onEndRefresh();
            }

        });

    }


}
