
package com.fei_ke.chiphellclient.bean;

/**
 * 回帖
 * 
 * @author 杨金阳
 * @2014-6-16
 */
public class Post {
    private String avatarUrl;
    String content;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Post [avatarUrl=" + avatarUrl + ", content=" + content + "]";
    }

}
