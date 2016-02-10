package com.fei_ke.chiphellclient.api.support;

/**
 * Created by fei on 16/1/28.
 */
public interface ObjectParser<T> {
    T parse(String content);
}
