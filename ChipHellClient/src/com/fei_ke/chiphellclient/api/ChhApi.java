
package com.fei_ke.chiphellclient.api;

import android.webkit.CookieManager;

import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.PlateGroup;
import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.bean.PrepareQuoteReply;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.bean.User;
import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.constant.Mode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

/**
 * CHH论坛接口
 */
public class ChhApi {
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
    public void getPlateGroups(ApiCallBack<List<PlateGroup>> apiCallBack) {
        getAsyncHttpClient().get(Constants.BASE_URL + "forum.php?mobile=yes", new ApiResponsHandler<List<PlateGroup>>(apiCallBack) {

            @Override
            public List<PlateGroup> onSuccessThenParse(String responseString) {
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
            public User onSuccessThenParse(String responseString) {
                User user = HtmlParse.parseUserInfo(responseString);
                return user;
            }

        });
    }

    /**
     * 获取帖子列表
     * 
     * @param plate 版块
     * @param page 页码
     * @param apiCallBack
     */
    public void getThreadList(Plate plate, int page, ApiCallBack<List<Thread>> apiCallBack) {
        RequestParams param = new RequestParams("page", page);
        getAsyncHttpClient().get(plate.getUrl(), param, new ApiResponsHandler<List<Thread>>(apiCallBack) {

            @Override
            public List<Thread> onSuccessThenParse(String responseString) {
                List<Thread> threads = HtmlParse.parseThreadList(responseString);
                return threads;
            }

        });
    }

    /**
     * 获取回复列表
     * 
     * @param thread 帖子
     * @param page 页码
     * @param apiCallBack
     */
    public void getPostList(Thread thread, int page, ApiCallBack<List<Post>> apiCallBack) {
        RequestParams param = new RequestParams("page", page);
        param.add("mobile", 2);// 回帖列表使用触屏版来解析
        getAsyncHttpClient().get(thread.getUrl(), param, new ApiResponsHandler<List<Post>>(apiCallBack) {

            @Override
            public List<Post> onSuccessThenParse(String responseString) {
                System.out.println(responseString);
                List<Post> posts = HtmlParse.parsePostList(responseString);
                return posts;
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
    public void reply(String fid, String tid, String formhash, String message, ApiCallBack<String> apiCallBack) {
        String url = Constants.BASE_URL + "forum.php?mod=post&action=reply&replysubmit=yes&mobile=yes";
        RequestParams param = new RequestParams();
        param.add("message", message);
        param.add("fid", fid);
        param.add("tid", tid);
        param.add("formhash", formhash);
        getAsyncHttpClient().post(url, param, new ApiResponsHandler<String>(apiCallBack) {

            @Override
            public String onSuccessThenParse(String responseString) {
                System.out.println(responseString);
                String message = "发送成功";
                Document document = Jsoup.parse(responseString);
                Element messagetext = document.getElementById("messagetext");
                if (messagetext != null) {
                    message = messagetext.child(0).text();
                }

                return message;
            }
        });
    }

    /**
     * 引用回复
     * 
     * @param quoteReply
     * @param apiCallBack
     */
    public void quotrReply(PrepareQuoteReply quoteReply, ApiCallBack<String> apiCallBack) {
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
        getAsyncHttpClient().post(Constants.BASE_URL+quoteReply.getUrl(), params, new ApiResponsHandler<String>(apiCallBack) {

            @Override
            public String onSuccessThenParse(String responseString) {
                System.out.println(responseString);
                String message = "发送成功";
                Document document = Jsoup.parse(responseString);
                Element messagetext = document.getElementById("messagetext");
                if (messagetext != null) {
                    message = messagetext.child(0).text();
                }

                return message;
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
            public PrepareQuoteReply onSuccessThenParse(String responseString) {
                PrepareQuoteReply prepareQuoteReply = HtmlParse.parsePrepareQuoteReply(responseString);
                return prepareQuoteReply;
            }
        });
    }

    private AsyncHttpClient getAsyncHttpClient() {
        if (mAsyncHttpClient == null) {
            mAsyncHttpClient = new AsyncHttpClient();
            mAsyncHttpClient.addHeader("Cookie", CookieManager.getInstance().getCookie(Constants.BASE_URL));
        }
        return mAsyncHttpClient;
    }
}
