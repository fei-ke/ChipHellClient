
package com.fei_ke.chiphellclient.bean;

/**
 * 引用回复的准备数据
 * 
 * @author fei-ke
 *         2014-6-20 上午10:28:43
 */
public class PrepareQuoteReply {
    String url;
    String quoteBody;
    String formhash;
    String message;
    String noticeauthor;
    String noticeauthormsg;
    String noticetrimstr;
    String posttime;
    String replysubmit = "yes";
    String reppid;
    String reppost;

    /*
     * 验证码相关
     * String seccodehash;
     * String seccodeverify;
     */

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQuoteBody() {
        return quoteBody;
    }

    public void setQuoteBody(String quoteBody) {
        this.quoteBody = quoteBody;
    }

    public String getFormhash() {
        return formhash;
    }

    public void setFormhash(String formhash) {
        this.formhash = formhash;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNoticeauthor() {
        return noticeauthor;
    }

    public void setNoticeauthor(String noticeauthor) {
        this.noticeauthor = noticeauthor;
    }

    public String getNoticeauthormsg() {
        return noticeauthormsg;
    }

    public void setNoticeauthormsg(String noticeauthormsg) {
        this.noticeauthormsg = noticeauthormsg;
    }

    public String getNoticetrimstr() {
        return noticetrimstr;
    }

    public void setNoticetrimstr(String noticetrimstr) {
        this.noticetrimstr = noticetrimstr;
    }

    public String getPosttime() {
        return posttime;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }

    public String getReppid() {
        return reppid;
    }

    public void setReppid(String reppid) {
        this.reppid = reppid;
    }

    public String getReplysubmit() {
        return replysubmit;
    }

    public String getReppost() {
        return reppost;
    }

    public void setReppost(String reppost) {
        this.reppost = reppost;
    }

    @Override
    public String toString() {
        return "PrepareQuoteReply [url=" + url + ", quoteBody=" + quoteBody + ", formhash=" + formhash + ", message=" + message
                + ", noticeauthor=" + noticeauthor + ", noticeauthormsg=" + noticeauthormsg + ", noticetrimstr=" + noticetrimstr
                + ", posttime=" + posttime + ", replysubmit=" + replysubmit + ", reppid=" + reppid + ", reppost=" + reppost + "]";
    }

}
