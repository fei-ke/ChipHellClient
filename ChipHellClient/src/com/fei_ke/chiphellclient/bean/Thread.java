
package com.fei_ke.chiphellclient.bean;

import com.fei_ke.chiphellclient.utils.UrlParamsMap;

/**
 * 帖子列表item
 * 
 * @author fei-ke
 * @2014-6-15
 */
public class Thread extends BaseBean {
    String title;
    String url;
    String by;
    String timeAndCount;
    String date;
    String count;
    String imgSrc;
    int titleColor;
    String tid;// 帖子id

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getDate() {
        calcDateAndCount();
        return date;
    }

    private void calcDateAndCount() {
        if (date == null || count == null) {
            timeAndCount = timeAndCount.trim();
            try {
                String[] s = timeAndCount.split("回");
                date = s[0];
                count = s[1];
            } catch (Exception e) {
                date = timeAndCount;
                count = "0";
            }
        }
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        calcDateAndCount();
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setTimeAndCount(String timeAndCount) {
        this.timeAndCount = timeAndCount;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    @Override
    public String toString() {
        return "Thread [title=" + title + ", url=" + url + ", by=" + by + ", timeAndCount=" + timeAndCount + "]";
    }

    public String getTid() {
        if (tid == null) {
            tid = new UrlParamsMap(url).get("tid");
        }
        return tid;
    }

}
