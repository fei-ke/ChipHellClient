package com.fei_ke.chiphellclient.api;

import android.text.TextUtils;

import com.android.volley.Request;
import com.fei_ke.chiphellclient.api.support.ApiCallBack;
import com.fei_ke.chiphellclient.api.support.ApiRequest;
import com.fei_ke.chiphellclient.api.support.ObjectParser;
import com.fei_ke.chiphellclient.api.support.RequestBuilder;
import com.fei_ke.chiphellclient.bean.AlbumWrap;
import com.fei_ke.chiphellclient.bean.AppUpdate;
import com.fei_ke.chiphellclient.bean.PlateGroup;
import com.fei_ke.chiphellclient.bean.PostListWrap;
import com.fei_ke.chiphellclient.bean.PrepareQuoteReply;
import com.fei_ke.chiphellclient.bean.ReplyResult;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.bean.ThreadListWrap;
import com.fei_ke.chiphellclient.bean.User;
import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.constant.Mode;

import java.util.List;

/**
 * CHH论坛接口
 */
public class ChhApi {
    protected static final String TAG = "ChhApi";
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
    public static ApiRequest<List<PlateGroup>> getPlateGroups() {

        return new RequestBuilder<List<PlateGroup>>()
                .url(Constants.BASE_URL + "forum.php?forumlist=1&mobile=1")
                .shouldCache()
                .objectParser(new ObjectParser<List<PlateGroup>>() {
                    @Override
                    public List<PlateGroup> parse(String content) {
                        return HtmlParse.parsePlateGroupList(content);
                    }
                })
                .build();
    }

    /**
     * 获取用户信息
     */
    public static ApiRequest<User> getUserInfo() {
        return new RequestBuilder<User>()
                .url(Constants.BASE_URL + "home.php?mod=space&mobile=2")
                .objectParser(new ObjectParser<User>() {
                    @Override
                    public User parse(String content) {
                        return HtmlParse.parseUserInfo(content);
                    }
                })
                .build();
    }

    /**
     * 获取帖子列表
     *
     * @param url  版块链接
     * @param page 页码
     */
    public static ApiRequest<ThreadListWrap> getThreadList(String url, final int page, String orderBy) {
        return new RequestBuilder<ThreadListWrap>()
                .url(url)
                .putParameter("page", page)
                .putParameterIf(!TextUtils.isEmpty(orderBy), "orderby", orderBy)
                .shouldCache(page == 1)
                .objectParser(new ObjectParser<ThreadListWrap>() {
                    @Override
                    public ThreadListWrap parse(String content) {
                        return HtmlParse.parseThreadList(content, page == 1);
                    }
                })
                .build();
    }

    /**
     * 获取回复列表
     *
     * @param thread 帖子
     * @param page   页码
     */
    public static ApiRequest<PostListWrap> getPostList(Thread thread, int page) {
        return new RequestBuilder<PostListWrap>()
                .url(thread.getUrl())
                .shouldCache()
                .putParameter("page", page)
                .putParameter("mobile", 2)// 回帖列表使用触屏版来解析
                .objectParser(new ObjectParser<PostListWrap>() {
                    @Override
                    public PostListWrap parse(String content) {
                        return HtmlParse.parsePostList(content);
                    }
                })
                .build();
    }

    /**
     * 回复主贴
     *
     * @param fid
     * @param tid
     * @param formhash
     * @param message
     */
    public static ApiRequest<ReplyResult> reply(String fid, String tid, String formhash, final String message) {
        return new RequestBuilder<ReplyResult>()
                .url(Constants.BASE_URL + "forum.php?mod=post&action=reply&replysubmit=yes&mobile=2")
                .method(Request.Method.POST)
                .putParameter("message", message)
                .putParameter("fid", fid)
                .putParameter("tid", tid)
                .putParameter("formhash", formhash)
                .objectParser(new ObjectParser<ReplyResult>() {
                    @Override
                    public ReplyResult parse(String content) {
                        ReplyResult result = new ReplyResult();

                        String messagetext = HtmlParse.parseMessageText(content);
                        if (messagetext != null) {
                            result.setMessage(messagetext);
                            return result;
                        }
                        result.setPostListWrap(HtmlParse.parsePostList(content));
                        return result;
                    }
                })
                .build();

        //String url = Constants.BASE_URL + "forum.php?mod=post&action=reply&replysubmit=yes&mobile=2";
        //RequestParams param = new RequestParams();
        //param.add("message", message);
        //param.add("fid", fid);
        //param.add("tid", tid);
        //param.add("formhash", formhash);
        //getAsyncHttpClient().post(url, param, new ApiResponsHandler<PostListWrap>(apiCallBack) {
        //
        //    @Override
        //    public PostListWrap parseResponse(String responseString) {
        //        LogMessage.i(TAG + "#quotrReply", responseString);
        //        String messagetext = HtmlParse.parseMessageText(responseString);
        //        if (messagetext != null) {
        //            sendFailureMessage(0, null, messagetext.getBytes(), new Throwable(messagetext));
        //            return null;
        //        }
        //        return HtmlParse.parsePostList(responseString);
        //    }
        //});
    }

    /**
     * 引用回复
     *
     * @param quoteReply
     */
    public static ApiRequest<ReplyResult> quotrReply(PrepareQuoteReply quoteReply) {
        return new RequestBuilder<ReplyResult>()
                .url(quoteReply.getUrl())
                .method(Request.Method.POST)
                .putParameter("formhash", quoteReply.getFormhash())
                .putParameter("message", quoteReply.getMessage())
                .putParameter("noticeauthor", quoteReply.getNoticeauthor())
                .putParameter("noticeauthormsg", quoteReply.getNoticeauthormsg())
                .putParameter("noticetrimstr", quoteReply.getNoticetrimstr())
                .putParameter("posttime", quoteReply.getPosttime())
                .putParameter("replysubmit", quoteReply.getReplysubmit())
                .putParameter("reppid", quoteReply.getReppid())
                .putParameter("reppost", quoteReply.getReppost())
                .objectParser(new ObjectParser<ReplyResult>() {
                    @Override
                    public ReplyResult parse(String content) {
                        ReplyResult result = new ReplyResult();

                        String messagetext = HtmlParse.parseMessageText(content);
                        if (messagetext != null) {
                            result.setMessage(messagetext);
                            return result;
                        }
                        result.setPostListWrap(HtmlParse.parsePostList(content));
                        return result;
                    }
                })
                .build();

        //RequestParams params = new RequestParams();
        //params.add("formhash", quoteReply.getFormhash());
        //params.add("message", quoteReply.getMessage());
        //params.add("noticeauthor", quoteReply.getNoticeauthor());
        //params.add("noticeauthormsg", quoteReply.getNoticeauthormsg());
        //params.add("noticetrimstr", quoteReply.getNoticetrimstr());
        //params.add("posttime", quoteReply.getPosttime());
        //params.add("replysubmit", quoteReply.getReplysubmit());
        //params.add("reppid", quoteReply.getReppid());
        //params.add("reppost", quoteReply.getReppost());
        //getAsyncHttpClient().post(quoteReply.getUrl(), params, new ApiResponsHandler<PostListWrap>(apiCallBack) {
        //
        //    @Override
        //    public PostListWrap parseResponse(String responseString) {
        //        LogMessage.i(TAG + "#quotrReply", responseString);
        //        String messagetext = HtmlParse.parseMessageText(responseString);
        //        if (messagetext != null) {
        //            sendFailureMessage(0, null, messagetext.getBytes(), new Throwable(messagetext));
        //            return null;
        //        }
        //        return HtmlParse.parsePostList(responseString);
        //    }
        //});
    }

    /**
     * 引用回复的请求表单准备
     *
     * @param url
     */
    public static ApiRequest<PrepareQuoteReply> prepareQuoteReply(String url) {
        return new RequestBuilder<PrepareQuoteReply>()
                .url(url)
                .objectParser(new ObjectParser<PrepareQuoteReply>() {
                    @Override
                    public PrepareQuoteReply parse(String content) {
                        return HtmlParse.parsePrepareQuoteReply(content);
                    }
                })
                .build();
    }

    /**
     * 获取图片列表
     *
     * @param url
     */
    public static ApiRequest<AlbumWrap> getAlbum(String url) {
        return new RequestBuilder<AlbumWrap>()
                .url(url)
                .objectParser(new ObjectParser<AlbumWrap>() {
                    @Override
                    public AlbumWrap parse(String content) {
                        return HtmlParse.parseAubum(content);
                    }
                })
                .build();
        //getAsyncHttpClient().get(url, new ApiResponsHandler<AlbumWrap>(apiCallBack) {
        //
        //    @Override
        //    public AlbumWrap parseResponse(String responseString) {
        //        LogMessage.i(TAG + "#getAlbum", responseString);
        //
        //        return HtmlParse.parseAubum(responseString);
        //    }
        //});

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

    public static ApiRequest<String> favorite(String id, int type, String formhash) {
        return new RequestBuilder<String>()
                .method(Request.Method.POST)
                .url(Constants.BASE_URL + "home.php")
                .putParameter("mod", "spacecp")
                .putParameter("ac", "favorite")
                .putParameterIf(type == TYPE_FORUM, "type", "forum")
                .putParameterIf(type == TYPE_THREAD, "type", "thread")
                .putParameter("id", id)
                .putParameter("formhash", formhash)
                .putParameter("mobile", "yes")
                .objectParser(new ObjectParser<String>() {
                    @Override
                    public String parse(String content) {
                        return HtmlParse.parseMessageText(content);
                    }
                })
                .build();
    }

    public static ApiRequest<String> deleteFavorite(String favid, String formhash) {
        return new RequestBuilder<String>()
                .url(Constants.BASE_URL + "home.php")
                .method(Request.Method.POST)
                .putParameter("mod", "spacecp")
                .putParameter("ac", "favorite")
                .putParameter("op", "delete")
                .putParameter("favid", favid)
                .putParameter("type", "forum")
                .putParameter("formhash", formhash)
                .putParameter("mobile", "yes")
                .putParameter("deletesubmit", "true")
                .objectParser(new ObjectParser<String>() {
                    @Override
                    public String parse(String content) {
                        return HtmlParse.parseMessageText(content);
                    }
                })
                .build();

        //String url = Constants.BASE_URL + "home.php";
        //RequestParams params = new RequestParams();
        //params.add("mod", "spacecp");
        //params.add("ac", "favorite");
        //params.add("op", "delete");
        //params.add("favid", favid);
        //params.add("type", "forum");
        //params.add("formhash", formhash);
        //params.add("mobile", "yes");
        //params.add("deletesubmit", "true");
        //getAsyncHttpClient().post(url, params, new ApiResponsHandler<String>(apiCallBack) {
        //
        //    @Override
        //    public String parseResponse(String responseString) {
        //        return HtmlParse.parseMessageText(responseString);
        //    }
        //});
    }

    public void checkAppUpdate(ApiCallBack<AppUpdate> apiCallBack) {
        //new AsyncHttpClient().get("http://fir.im/api/v2/app/version/548cf2e12b17bad661000596",
        //        new ApiResponsHandler<AppUpdate>(apiCallBack) {
        //
        //            @Override
        //            public AppUpdate parseResponse(String responseString) {
        //                return new AppUpdate().fromJson(responseString);
        //            }
        //        });
    }

    //private AsyncHttpClient getAsyncHttpClient() {
    //    if (mAsyncHttpClient == null) {
    //        mAsyncHttpClient = new AsyncHttpClient();
    //        mAsyncHttpClient.setTimeout(30 * 1000);
    //        mAsyncHttpClient.addHeader("Cookie", CookieManager.getInstance().getCookie(Constants.BASE_URL));
    //        mAsyncHttpClient.setCookieStore(new PersistentCookieStore(ChhApplication.getInstance()));
    //        mAsyncHttpClient.addHeader("Content-Type", "application/x-www-form-urlencoded");
    //    }
    //    return mAsyncHttpClient;
    //}
}
