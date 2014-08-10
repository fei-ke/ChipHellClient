package com.fei_ke.chiphellclient.bean;

import com.fei_ke.chiphellclient.utils.UrlParamsMap;

/**
 * 版块
 *
 * @author fei-ke
 * @2014-6-14
 */
public class Plate extends BaseBean {
    private String title;
    private String url;
    private String xg1;// 今日帖数
    private String fid;// 版块id
    private String favoriteId;
    private boolean isSubPlate;// 是否是子版块

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

    public String getXg1() {
        return xg1 == null ? "(0)" : xg1;
    }

    public void setXg1(String xg1) {
        this.xg1 = xg1;
    }

    @Override
    public String toString() {
        return title;
    }

    public boolean isSubPlate() {
        return isSubPlate;
    }

    public void setSubPlate(boolean isSubPlate) {
        this.isSubPlate = isSubPlate;
    }

    public boolean isFavorite() {
        return favoriteId != null;
    }

    public String getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(String favoriteId) {
        this.favoriteId = favoriteId;
    }


    public String getFid() {
        if (fid == null) {
            fid = new UrlParamsMap(url).get("fid");
        }
        return fid;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Plate) {
            if (((Plate) o).getFid().equals(this.getFid())) {
                return true;
            }
        }
        return false;
    }
}
