package com.fei_ke.chiphellclient.bean;

import android.text.TextUtils;

/**
 * 回帖结果
 * Created by fei-ke on 2016/2/17.
 */
public class ReplyResult extends BaseBean {
    PostListWrap postListWrap;
    String message;

    public boolean isSuccess() {
        return TextUtils.isEmpty(message) && postListWrap != null;
    }

    public PostListWrap getPostListWrap() {
        return postListWrap;
    }

    public void setPostListWrap(PostListWrap postListWrap) {
        this.postListWrap = postListWrap;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
