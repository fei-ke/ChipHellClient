package com.fei_ke.chiphellclient.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.fei_ke.chiphellclient.ui.customviews.MySlidingUpPanelLayout;
import com.fei_ke.chiphellclient.ui.fragment.FastReplyFragment;
import com.fei_ke.chiphellclient.ui.fragment.FastReplyFragment.OnReplySuccess;
import com.fei_ke.chiphellclient.ui.fragment.PostListFragment;
import com.fei_ke.chiphellclient.utils.ThreadStatusUtil;
import com.fei_ke.chiphellclient.utils.ToastUtil;

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
    MySlidingUpPanelLayout mPanelLayout;

    @ViewById(R.id.refreshLayout)
    PullToRefreshLayout mRefreshLayout;

    @ViewById
    TextView textViewTotalPage;

    @ViewById
    Spinner spinnerPage;
    @ViewById(R.id.dragView)
    View dragView;
    PostPageAdapter mPostPageAdapter;
    private boolean mIsFreshing;
    private float webViewContentScale;


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

        spinnerPage.setTag(0);
        spinnerPage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((Integer) spinnerPage.getTag() == position) return;

                viewPagerPost.setCurrentItem(position, false);
                if (!mPanelLayout.isPanelExpanded()) {
                    mPanelLayout.expandPanel();
                }

                spinnerPage.setTag(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewPagerPost.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                spinnerPage.setSelection(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mFastReplyFragment.setOnReplySuccess(new OnReplySuccess() {

            @Override
            public void onSuccess(List<Post> posts) {
                // 回复完之后更新列表
                viewPagerPost.setCurrentItem(mPostPageAdapter.getCount() - 1, false);
                PostListFragment lastPostListFragment = mPostPageAdapter.getPostFragment(mPostPageAdapter.getCount() - 1);
                if (lastPostListFragment != null) {
                    lastPostListFragment.update(posts);
                }
            }
        });
        initWebView();
        hookPanelTouchEvent();
        getPostList();
    }


    private void hookPanelTouchEvent() {
        final ViewConfiguration vc = ViewConfiguration.get(this);
        mPanelLayout.setHookDispatchTouchEvent(new MySlidingUpPanelLayout.HookDispatchTouchEvent() {
            float offsetY;
            float lastPosY = -1;
            float lastPosX = -1;
            float curPosY;

            boolean needForward;
            boolean readyForward;
            boolean forwarding;
            boolean hasCalcOffset;

            @Override
            public boolean dispatchTouchEvent(MotionEvent event) {
                int action = event.getAction();
                curPosY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastPosX = event.getX();
                }

                PostListFragment curPostListFra = mPostPageAdapter.getPostFragment(viewPagerPost.getCurrentItem());

                boolean isPanelExpanded = mPanelLayout.isPanelExpanded();

                //是否需要转发触摸事件
                needForward = (!isPanelExpanded && isWebViewToBottom()) ||
                        (isPanelExpanded && curPostListFra != null && curPostListFra.isListOnTop());

                if (needForward && !hasCalcOffset) {
                    //计算偏移
                    int[] loc = new int[2];
                    dragView.getLocationOnScreen(loc);
                    offsetY = loc[1] - event.getRawY();

                    lastPosY = curPosY;
                    hasCalcOffset = true;
                }

                float diffY = curPosY - lastPosY;
                float diffX = event.getX() - lastPosX;
                readyForward = needForward
                        && (diffX < vc.getScaledTouchSlop())
                        && (isPanelExpanded
                        ? diffY > vc.getScaledTouchSlop()
                        : diffY < -vc.getScaledTouchSlop());

                if (readyForward && !forwarding) {
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    mPanelLayout.callSuperDispatchTouchEvent(event);
                    event.setAction(MotionEvent.ACTION_DOWN);
                    forwarding = true;
                }


                if (forwarding) {//设置偏移
                    event.offsetLocation(0, offsetY + dragView.getHeight() / 2);
                    event.setLocation(dragView.getWidth() / 2, event.getY());
                }

                if (action == MotionEvent.ACTION_UP) {
                    forwarding = false;
                    hasCalcOffset = false;
                    needForward = false;
                    readyForward = false;
                    lastPosY = -1;
                    lastPosX = -1;
                }
//                LogMessage.i("HookDispatchTouchEvent", this);
                return false;
            }

            @Override
            public String toString() {
                return "$classname{" +
                        "offsetY=" + offsetY +
                        ", lastPosY=" + lastPosY +
                        ", curPosY=" + curPosY +
                        ", needForward=" + needForward +
                        ", readyForward=" + readyForward +
                        ", forwarding=" + forwarding +
                        ", hasCalcOffset=" + hasCalcOffset +
                        '}';
            }
        });
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

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
                webViewContentScale = newScale;
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

    private boolean isWebViewToBottom() {
//        if (webViewContentScale == 0) {
//            webViewContentScale = webViewContent.getScale();
//        }
        //WebView的总高度
        float webViewContentHeight = FloatMath.floor(webViewContent.getContentHeight() * webViewContent.getScale());
        //WebView的现高度
        float webViewCurrentHeight = (webViewContent.getHeight() + webViewContent.getScrollY());

        return webViewCurrentHeight >= webViewContentHeight;
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
                int totalPage = result.getTotalPage();
                mPostPageAdapter.setSize(totalPage);
                textViewTotalPage.setText(totalPage + "");
                String[] spinnerPageData = new String[totalPage];
                for (int i = 0; i < totalPage; i++) {
                    spinnerPageData[i] = i + 1 + "";
                }
                spinnerPage.setAdapter(new ArrayAdapter<String>(ThreadDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerPageData));

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

    public View getReplyPanel() {
        return mlayoutFastReply;
    }
}
