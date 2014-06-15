
package com.fei_ke.chiphellclient.bean;

/**
 * 帖子列表item
 * 
 * @author 杨金阳
 * @2014-6-15
 */
public class Thread extends BaseBean {
    String title;
    String url;
    String by;
    String timeAndCount;
    String date;
    String count;

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
            String[] s = timeAndCount.trim().split(" ");
            date = s[1];
            count = s[s.length - 1];
            System.out.println(date+" "+count);
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

    @Override
    public String toString() {
        return "Thread [title=" + title + ", url=" + url + ", by=" + by + ", timeAndCount=" + timeAndCount + "]";
    }

}
