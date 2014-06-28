
package com.fei_ke.chiphellclient.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.api.ApiCallBack;
import com.fei_ke.chiphellclient.api.ChhApi;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.bean.PrepareQuoteReply;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.ui.adapter.PostListAdapter;
import com.fei_ke.chiphellclient.ui.fragment.FastReplyFragment;
import com.fei_ke.chiphellclient.ui.fragment.FastReplyFragment.OnReplySuccess;
import com.fei_ke.chiphellclient.utils.ThreadStatusUtil;
import com.fei_ke.chiphellclient.utils.ToastUtil;
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
 * @author fei-ke
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

    // @ViewById(R.id.main_post)
    // PostMainView mMainPostView;
    @ViewById(R.id.webView_content)
    WebView webViewContent;

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
        if (mThread == null) {
            handExportUrl();
        }

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
        mRefreshListView.setOnScrollListener(onScrollListener);
        mRefreshListView.setOnPullEventListener(new OnPullEventListener<ListView>() {

            @Override
            public void onPullEvent(PullToRefreshBase<ListView> refreshView, State state, Mode direction) {
                if (state.equals(State.PULL_TO_REFRESH) && direction.equals(Mode.PULL_FROM_START)) {
                    mPanelLayout.collapsePanel();
                }
            }
        });

        mFastReplyFragment.setOnReplySuccess(new OnReplySuccess() {

            @Override
            public void onSuccess(List<Post> posts) {
                mPostListAdapter.update(posts);
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
        if (url.indexOf("from=album") != -1) {
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

    private OnScrollListener onScrollListener = new OnScrollListener() {
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

    void showReplyPanel() {
        if (mlayoutFastReply.getVisibility() != View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_view_anim);
            mlayoutFastReply.startAnimation(animation);
            mlayoutFastReply.setVisibility(View.VISIBLE);
            // mFastReplyFragment.show();
        }
    }

    void hideReplyPanel() {
        if (mlayoutFastReply.getVisibility() == View.VISIBLE) {
            mFastReplyFragment.hide();
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_view_anim);
            mlayoutFastReply.startAnimation(animation);
            mlayoutFastReply.setVisibility(View.GONE);
        }
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
        api.getPostList(this.getApplicationContext(), mThread, page, new ApiCallBack<List<Post>>() {
            @Override
            public void onStart() {
                onStartRefresh();
            }

            @Override
            public void onCache(List<Post> result) {
                if (result == null || result.size() == 0) {
                    return;
                }

                if (page == 1) {
                    loadMainContent(result.get(0));
                    mPostListAdapter.clear();
                }
                mPostListAdapter.update(result);
            }

            @Override
            public void onSuccess(List<Post> result) {
                if (result == null || result.size() == 0) {
                    return;
                }

                if (page == 1) {
                    loadMainContent(result.get(0));
                    mPostListAdapter.clear();

                    // 将该帖子设为已读
                    new ThreadStatusUtil(getApplicationContext()).setRead(mThread.getTid());
                }
                boolean hasNewData = mPostListAdapter.update(result);
                if (!hasNewData) {
                    // mPage -= 1;
                    // TODO页码处理
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                ToastUtil.show(ThreadDetailActivity.this, "oops 刷新失败了");
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

        String url = post.getReplyUrl();
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(getApplicationContext(), R.string.not_login, Toast.LENGTH_SHORT).show();
            return true;
        }
        ChhApi api = new ChhApi();
        api.prepareQuoteReply(post.getReplyUrl(), new ApiCallBack<PrepareQuoteReply>() {
            ProgressDialog dialog;

            @Override
            public void onStart() {
                dialog = new ProgressDialog(ThreadDetailActivity.this);
                dialog.setMessage("正在准备……");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }

            @Override
            public void onSuccess(PrepareQuoteReply result) {
                mFastReplyFragment.setPrepareQuoteReply(result);
                if (dialog.isShowing()) {
                    showReplyPanel();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                error.printStackTrace();
                ToastUtil.show(ThreadDetailActivity.this, "oops 获取失败了");
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
