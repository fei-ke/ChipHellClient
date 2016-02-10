package com.fei_ke.chiphellclient.api.support;

/**
 * 请求参数
 * Created by 杨金阳 on 2015/7/28.
 */
public interface RequestParams {

    byte[] getPostBody();

    String getParamString();

    String getContentType();

}
