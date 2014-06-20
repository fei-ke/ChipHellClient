
package com.fei_ke.chiphellclient.bean;

/**
 * 回帖
 * 
 * @author 杨金阳
 * @2014-6-16
 */
public class Post {
    private String avatarUrl;
    private String replyUrl;
    String content;
    String authi;// 名字，楼层，时间 html

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getReplyUrl() {
        return replyUrl;
    }

    public void setReplyUrl(String replyUrl) {
        this.replyUrl = replyUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthi() {
        return authi;
    }

    public void setAuthi(String authi) {
        this.authi = authi;
    }

    @Override
    public String toString() {
        return "Post [avatarUrl=" + avatarUrl + ", replyUrl=" + replyUrl + ", content=" + content + ", authi=" + authi + "]";
    }

 

}
