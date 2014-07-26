
package com.fei_ke.chiphellclient.bean;

import com.fei_ke.chiphellclient.utils.UrlParamsMap;

/**
 * 版块分类
 * 
 * @author fei-ke
 * @2014-6-14
 */
public class PlateClass extends Plate {
    String title;
    String url;
    String fid;// 版块id

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

    @Override
    public String toString() {
        return title;
    }

    public String getFid() {
        if (fid == null) {
            fid = new UrlParamsMap(url).get("fid");
        }
        return fid;
    }

}
