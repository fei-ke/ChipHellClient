package com.fei_ke.chiphellclient.event;

import com.fei_ke.chiphellclient.bean.Plate;

import java.util.List;

/**
 * 版块收藏状态发生变化事件
 * Created by fei-ke on 2014/8/10.
 */
public class FavoriteChangeEvent {
    List<Plate> favoritePlate;

    public FavoriteChangeEvent(List<Plate> favoritePlate) {
        this.favoritePlate = favoritePlate;
    }

    public FavoriteChangeEvent() {
    }

    public List<Plate> getFavoritePlate() {
        return favoritePlate;
    }

    public void setFavoritePlate(List<Plate> favoritePlate) {
        this.favoritePlate = favoritePlate;
    }
}
