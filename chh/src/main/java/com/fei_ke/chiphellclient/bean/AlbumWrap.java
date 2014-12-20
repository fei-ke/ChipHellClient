
package com.fei_ke.chiphellclient.bean;

import java.util.List;

/**
 * 相册
 * 
 * @author fei-ke
 * @2014-6-22
 */
public class AlbumWrap extends BaseBean {
    List<String> urls;
    int curPosition;

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public int getCurPosition() {
        return curPosition;
    }

    public void setCurPosition(int curPosition) {
        this.curPosition = curPosition;
    }

    @Override
    public String toString() {
        return "AlbumWrap [urls=" + urls + ", curPosition=" + curPosition + "]";
    }

}
