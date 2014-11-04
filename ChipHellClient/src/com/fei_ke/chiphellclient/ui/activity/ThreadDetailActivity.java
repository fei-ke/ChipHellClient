package com.fei_ke.chiphellclient.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.fei_ke.chiphellclient.ChhApplication;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.bean.PostListWrap;
import com.fei_ke.chiphellclient.bean.PrepareQuoteReply;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.ui.adapter.PostPageAdapter;
import com.fei_ke.chiphellclient.ui.fragment.FastReplyFragment;
import com.fei_ke.chiphellclient.ui.fragment.FastReplyFragment.OnReplySuccess;
import com.fei_ke.chiphellclient.ui.fragment.PostListFragment;
import com.fei_ke.chiphellclient.utils.ThreadStatusUtil;
import com.fei_ke.chiphellclient.utils.ToastUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * 帖子内容
 *
 * @author fei-ke
 * @2014-6-15
 */
@EActivity(R.layout.activity_thread_detail)
public class ThreadDetailActivity extends BaseActivity {

    private static final String TAG = "ThreadDetailActivity";
    @ViewById
    ViewPager viewPagerPost;
    @ViewById(R.id.name)
    TextView textViewName;

    @ViewById
    ViewGroup layoutSlideUp;

    @Extra
    Plate mPlate;

    @Extra
    Thread mThread;

    @FragmentByTag("fast_reply")
    FastReplyFragment mFastReplyFragment;

    // @ViewById(R.id.main_post)
    // PostMainView mMainPostView;
    @ViewById(R.id.webView_content)
    WebView webViewContent;

    @ViewById(R.id.layout_fast_reply)
    View mlayoutFastReply;

    @ViewById(R.id.sliding_layout)
    SlidingUpPanelLayout mPanelLayout;

    @ViewById(R.id.refreshLayout)
    PullToRefreshLayout mRefreshLayout;

    PostPageAdapter mPostPageAdapter;
    private boolean mIsFreshing;


    public static Intent getStartIntent(Context context, Plate plate, Thread thread) {
        return ThreadDetailActivity_.intent(context).mThread(thread).mPlate(plate).get();
    }

    @Override
    protected void onAfterViews() {
        // mRefreshListView.setMode(Mode.DISABLED);
        if (mThread == null) {
            handExportUrl();
        }

        mFastReplyFragment.setPlateAndThread(mPlate, mThread);


        setTitle(mPlate.getTitle());
        getActionBar().setSubtitle(mThread.getTitle());
        mPostPageAdapter = new PostPageAdapter(getSupportFragmentManager(), mThread);
        viewPagerPost.setAdapter(mPostPageAdapter);

        OnRefreshListener onRefreshListener = new OnRefreshListener() {
            @Override
            public void onRefreshStarted(View view) {
                getPostList();
            }
        };
        ActionBarPullToRefresh.from(this)
                .allChildrenArePullable()
                .listener(onRefreshListener)
                .options(Options.create()
                        .scrollDistance(.30f)
                        .build())
                .setup(mRefreshLayout);

        // mRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
        //
        // @Override
        // public void onLastItemVisible() {
        // if (!mIsFreshing) {
        // getPostList(++mPage);
        // }
        // }
        // });
        //TODO fei-ke 2014/11/1 收起
//        mRefreshListView.setMode(Mode.BOTH);
//        mRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
//
//            // 下拉刷新，刷新状态在顶部
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                if (!mIsFreshing) {
//                    getPostList();
//                }
//            }
//
//            // 滑到底部刷新，刷新状态在底部
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                if (!mIsFreshing) {
//                    getPostList(++mPage);
//                }
//            }
//        });


        //TODO fei-ke 2014/11/1  
//        mRefreshListView.setOnPullEventListener(new OnPullEventListener<ListView>() {
//
//            @Override
//            public void onPullEvent(PullToRefreshBase<ListView> refreshView, State state, Mode direction) {
//                if (state.equals(State.PULL_TO_REFRESH) && direction.equals(Mode.PULL_FROM_START)) {
//                    mPanelLayout.collapsePanel();
//                }
//            }
//        });

        mFastReplyFragment.setOnReplySuccess(new OnReplySuccess() {

            @Override
            public void onSuccess(List<Post> posts) {
                // 回复完之后更新列表
                viewPagerPost.setCurrentItem(mPostPageAdapter.getCount() - 1,false);
                PostListFragment lastPostListFragment = mPostPageAdapter.getPostFragment(mPostPageAdapter.getCount() - 1);
                if (lastPostListFragment != null) {
                    lastPostListFragment.update(posts);
                }
            }
        });
        initWebView();
        getPostList();
    }


    private void handExportUrl() {
        String url = getIntent().getDataString();
        this.mThread = new Thread();
        mThread.setUrl(url);
        mPlate = new Plate();
        mPlate.setTitle("返回");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        webViewContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 回复
                return handleUrl(url);
            }
        });
        webViewContent.setWebChromeClient(new WebChromeClient());

        WebSettings settings = webViewContent.getSettings();

        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webViewContent.setVerticalScrollBarEnabled(true);
        webViewContent.setHorizontalScrollBarEnabled(false);
        settings.setJavaScriptEnabled(true);
    }

    private boolean handleUrl(String url) {
        // 回复链接，比如回复可见时
        if (url.startsWith(Constants.BASE_URL + "forum.php?mod=post&action=reply")) {
            mPanelLayout.expandPanel();
            return true;
        }
        // 点击图片
        if (url.contains("from=album")) {
            Intent intent = AlbumActivity.getStartIntent(ThreadDetailActivity.this, url);
            startActivity(intent);
            return true;
        }
        // 登录
        if (url.startsWith(Constants.BASE_URL + "member.php?mod=logging&action=login")) {
            Intent intent = LoginActivity.getStartIntent(this);
            startActivityForResult(intent, MainActivity.REQUEST_CODE_LOGIN);
            return true;
        }
        // 其他链接
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(resultCode, requestCode, data);
        if (requestCode == MainActivity.REQUEST_CODE_LOGIN && resultCode == RESULT_OK) {
            //登录之后刷新内容，比如回复可见
            getPostList();
        }
    }

    public void loadMainContent(Post post) {
        String content = post.getContent();
        if (post.getImgList() != null) {
            content += post.getImgList();
        }
        webViewContent.loadDataWithBaseURL(Constants.BASE_URL, content, "text/html", "utf-8", null);
    }


    public void showReplyPanel() {
        if (mlayoutFastReply.getVisibility() != View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_view_anim);
            mlayoutFastReply.startAnimation(animation);
            mlayoutFastReply.setVisibility(View.VISIBLE);
            // mFastReplyFragment.show();
        }
    }

    public void hideReplyPanel() {
        if (mlayoutFastReply.getVisibility() == View.VISIBLE) {
            mFastReplyFragment.hide();
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_view_anim);
            mlayoutFastReply.startAnimation(animation);
            mlayoutFastReply.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                //TODO 这里应该刷新当前评论所在页或者主贴
                if (!mPanelLayout.isPanelExpanded()) {
                    getPostList();
                } else {
                    PostListFragment curPostListFragment = mPostPageAdapter.getPostFragment(viewPagerPost.getCurrentItem());
                    if (curPostListFragment != null) {
                        curPostListFragment.update();
                    }
                }
                return true;
            case R.id.action_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mThread.getUrl()));
                startActivity(intent);
                return true;
            case R.id.action_favorite:
                favorite();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void favorite() {
        new ChhApi().favorite(mThread.getTid(), ChhApi.TYPE_THREAD, ChhApplication.getInstance().getFormHash(), new ApiCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                ToastUtil.show(getApplicationContext(), result);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.thread_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void setPrepareQuoteReply(PrepareQuoteReply quoteReply) {
        mFastReplyFragment.setPrepareQuoteReply(quoteReply);
    }

    private void getPostList() {
        if (mIsFreshing) {
            return;
        }
        mIsFreshing = true;

        ChhApi api = new ChhApi();
        api.getPostList(this, mThread, 1, new ApiCallBack<PostListWrap>() {
            @Override
            public void onStart() {
                onStartRefresh();
                mRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onCache(PostListWrap result) {
                if (result == null || result.size() == 0) {
                    return;
                }

                loadMainContent(result.getPosts().get(0));
            }

            @Override
            public void onSuccess(PostListWrap result) {
                if (result == null || result.size() == 0) {
                    return;
                }

                loadMainContent(result.getPosts().get(0));
                mPostPageAdapter.setSize(result.getTotalPage());
                // 将该帖子设为已读
                new ThreadStatusUtil(getApplicationContext()).setRead(mThread.getTid());
            }

            @Override
            public void onFailure(Throwable error, String content) {
                ToastUtil.show(ThreadDetailActivity.this, "oops 刷新失败了");
            }

            @Override
            public void onFinish() {
                mIsFreshing = false;
                //刷新完成
                mRefreshLayout.setRefreshComplete();
                onEndRefresh();
            }

        });

    }

}
