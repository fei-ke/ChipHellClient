package com.fei_ke.chiphellclient.api;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;

import com.fei_ke.chiphellclient.ChhApplication;
import com.fei_ke.chiphellclient.bean.AlbumWrap;
import com.fei_ke.chiphellclient.bean.PlateGroup;
import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.bean.PrepareQuoteReply;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.bean.ThreadListWrap;
import com.fei_ke.chiphellclient.bean.User;
import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.constant.Mode;
import com.fei_ke.chiphellclient.utils.LogMessage;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import java.util.List;

/**
 * CHH论坛接口
 */
public class ChhApi {
    protected static final String TAG = "ChhApi";
    private AsyncHttpClient mAsyncHttpClient;
    private Mode mMode;

    public ChhApi() {
        this(Mode.MOBILE);
    }

    public ChhApi(Mode mode) {
        this.mMode = mode;
    }

    /**
     * 获取版块列表
     */
    public void getPlateGroups(Context context, ApiCallBack<List<PlateGroup>> apiCallBack) {
        getAsyncHttpClient().get(context, Constants.BASE_URL + "forum.php?mobile=yes", true,
                new ApiResponsHandler<List<PlateGroup>>(apiCallBack) {

                    @Override
                    public List<PlateGroup> parseResponse(String responseString) {
                        List<PlateGroup> groups = HtmlParse.parsePlateGroupList(responseString);
                        return groups;
                    }
                });
    }

    /**
     * 获取用户信息
     *
     * @param apiCallBack
     */
    public void getUserInfo(ApiCallBack<User> apiCallBack) {
        getAsyncHttpClient().get(Constants.BASE_URL + "home.php?mod=space&mobile=2", new ApiResponsHandler<User>(apiCallBack) {

            @Override
            public User parseResponse(String responseString) {
                User user = HtmlParse.parseUserInfo(responseString);
                return user;
            }

        });
    }

    /**
     * 获取帖子列表
     *
     * @param url         版块链接
     * @param page        页码
     * @param apiCallBack
     */
    public void getThreadList(Context context, String url, final int page, String orderBy, ApiCallBack<ThreadListWrap> apiCallBack) {
        RequestParams param = new RequestParams("page", page);
        if (!TextUtils.isEmpty(orderBy)) {
            param.add("orderby", orderBy);
        }
        getAsyncHttpClient().get(context, url, param, page == 1, new ApiResponsHandler<ThreadListWrap>(apiCallBack) {

            @Override
            public ThreadListWrap parseResponse(String responseString) {
                ThreadListWrap threadListWrap = HtmlParse.parseThreadList(responseString, page == 1);
                return threadListWrap;
            }

        });
    }

    /**
     * 获取回复列表
     *
     * @param thread      帖子
     * @param page        页码
     * @param apiCallBack
     */
    public void getPostList(Context context, Thread thread, int page, ApiCallBack<List<Post>> apiCallBack) {
        RequestParams param = new RequestParams("page", page);
        param.add("mobile", 2);// 回帖列表使用触屏版来解析
        getAsyncHttpClient().get(context, thread.getUrl(), param, true, new ApiResponsHandler<List<Post>>(apiCallBack) {

            @Override
            public List<Post> parseResponse(String responseString) {
                LogMessage.i(TAG + "#getPostList", responseString);
                return HtmlParse.parsePostList(responseString);
            }

        });
    }

    /**
     * 回复主贴
     *
     * @param fid
     * @param tid
     * @param formhash
     * @param message
     * @param apiCallBack
     */
    public void reply(String fid, String tid, String formhash, String message, ApiCallBack<List<Post>> apiCallBack) {
        String url = Constants.BASE_URL + "forum.php?mod=post&action=reply&replysubmit=yes&mobile=2";
        RequestParams param = new RequestParams();
        param.add("message", message);
        param.add("fid", fid);
        param.add("tid", tid);
        param.add("formhash", formhash);
        getAsyncHttpClient().post(url, param, new ApiResponsHandler<List<Post>>(apiCallBack) {

            @Override
            public List<Post> parseResponse(String responseString) {
                LogMessage.i(TAG + "#quotrReply", responseString);
                String messagetext = HtmlParse.parseMessageText(responseString);
                if (messagetext != null) {
                    sendFailureMessage(0, null, messagetext.getBytes(), new Throwable(messagetext));
                    return null;
                }
                return HtmlParse.parsePostList(responseString);
            }
        });
    }

    /**
     * 引用回复
     *
     * @param quoteReply
     * @param apiCallBack
     */
    public void quotrReply(PrepareQuoteReply quoteReply, ApiCallBack<List<Post>> apiCallBack) {
        RequestParams params = new RequestParams();
        params.add("formhash", quoteReply.getFormhash());
        params.add("message", quoteReply.getMessage());
        params.add("noticeauthor", quoteReply.getNoticeauthor());
        params.add("noticeauthormsg", quoteReply.getNoticeauthormsg());
        params.add("noticetrimstr", quoteReply.getNoticetrimstr());
        params.add("posttime", quoteReply.getPosttime());
        params.add("replysubmit", quoteReply.getReplysubmit());
        params.add("reppid", quoteReply.getReppid());
        params.add("reppost", quoteReply.getReppost());
        getAsyncHttpClient().post(quoteReply.getUrl(), params, new ApiResponsHandler<List<Post>>(apiCallBack) {

            @Override
            public List<Post> parseResponse(String responseString) {
                LogMessage.i(TAG + "#quotrReply", responseString);
                String messagetext = HtmlParse.parseMessageText(responseString);
                if (messagetext != null) {
                    sendFailureMessage(0, null, messagetext.getBytes(), new Throwable(messagetext));
                    return null;
                }
                return HtmlParse.parsePostList(responseString);
            }
        });
    }

    /**
     * 引用回复的请求表单准备
     *
     * @param url
     * @param apiCallBack
     */
    public void prepareQuoteReply(String url, ApiCallBack<PrepareQuoteReply> apiCallBack) {
        getAsyncHttpClient().get(url, new ApiResponsHandler<PrepareQuoteReply>(apiCallBack) {

            @Override
            public PrepareQuoteReply parseResponse(String responseString) {
                return HtmlParse.parsePrepareQuoteReply(responseString);
            }
        });
    }

    /**
     * 获取图片列表
     *
     * @param url
     * @param apiCallBack
     */
    public void getAlbum(String url, ApiCallBack<AlbumWrap> apiCallBack) {
        getAsyncHttpClient().get(url, new ApiResponsHandler<AlbumWrap>(apiCallBack) {

            @Override
            public AlbumWrap parseResponse(String responseString) {
                LogMessage.i(TAG + "#getAlbum", responseString);

                return HtmlParse.parseAubum(responseString);
            }
        });

    }

    /**
     * 收藏版块
     *
     * @param id
     * @param formhash
     * @param apiCallBack
     */
    public static final int TYPE_FORUM = 0;
    public static final int TYPE_THREAD = 1;

    public void favorite(String id, int type, String formhash, ApiCallBack<String> apiCallBack) {
        String url = Constants.BASE_URL + "home.php";
        RequestParams params = new RequestParams();
        params.add("mod", "spacecp");
        params.add("ac", "favorite");
        if (type == TYPE_FORUM) {
            params.add("type", "forum");
        } else if (type == TYPE_THREAD) {
            params.add("type", "thread");
        }
        params.add("id", id);
        params.add("formhash", formhash);
        params.add("mobile", "yes");
        getAsyncHttpClient().post(url, params, new ApiResponsHandler<String>(apiCallBack) {

            @Override
            public String parseResponse(String responseString) {
                return HtmlParse.parseMessageText(responseString);
            }
        });
    }

    public void deleteFavorite(String favid, String formhash, ApiCallBack<String> apiCallBack) {
        String url = Constants.BASE_URL + "home.php";
        RequestParams params = new RequestParams();
        params.add("mod", "spacecp");
        params.add("ac", "favorite");
        params.add("op", "delete");
        params.add("favid", favid);
        params.add("type", "forum");
        params.add("formhash", formhash);
        params.add("mobile", "yes");
        params.add("deletesubmit", "true");
        getAsyncHttpClient().post(url, params, new ApiResponsHandler<String>(apiCallBack) {

            @Override
            public String parseResponse(String responseString) {
                return HtmlParse.parseMessageText(responseString);
            }
        });
    }

    private AsyncHttpClient getAsyncHttpClient() {
        if (mAsyncHttpClient == null) {
            mAsyncHttpClient = new AsyncHttpClient();
            mAsyncHttpClient.setTimeout(30 * 1000);
            mAsyncHttpClient.addHeader("Cookie", CookieManager.getInstance().getCookie(Constants.BASE_URL));
            mAsyncHttpClient.setCookieStore(new PersistentCookieStore(ChhApplication.getInstance()));
            mAsyncHttpClient.addHeader("Content-Type", "application/x-www-form-urlencoded");
        }
        return mAsyncHttpClient;
    }
}
