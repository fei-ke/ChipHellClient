
package com.fei_ke.chiphellclient.bean;

/**
 * 回帖
 * 
 * @author fei-ke
 * @2014-6-16
 */
public class Post {
    private String avatarUrl;
    private String replyUrl;
    private String content;
    private String authi;// 名字，楼层，时间 html
    private String imgList;// 图片附件列表

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

    public String getImgList() {
        return imgList;
    }

    public void setImgList(String imgList) {
        this.imgList = imgList;
    }

    @Override
    public String toString() {
        return "Post [avatarUrl=" + avatarUrl + ", replyUrl=" + replyUrl + ", content=" + content + ", authi=" + authi + "]";
    }

}
