package com.fei_ke.chiphellclient.api.support;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;

import java.io.File;

/**
 * Created by 杨金阳 on 2015/11/19.
 */
public class RequestBuilder<T> implements Request.Method {
    private String url;                 //url
    private ApiParams params;           //参数
    private boolean shouldCache;        //是否缓存
    private long cacheTime;             //缓存时间（毫秒）
    private int method;                 //请求方法
    private String cacheKey;            //缓存key，也用来唯一标识一次请求，重复的请求有可能会被合并
    private RetryPolicy retryPolicy;    //重试政策
    private int timeout = 10 * 1000;    //超时时间
    private int maxRetryCount = 0;      //重试次数
    private ObjectParser<T> objectParser;//解析器

    public RequestBuilder<T> url(String url) {
        this.url = url;
        return this;
    }

    public RequestBuilder<T> shouldCache(boolean shouldCache) {
        this.shouldCache = shouldCache;
        return this;
    }

    public RequestBuilder<T> shouldCache() {
        return shouldCache(true);
    }    //==================参数======================

    public RequestBuilder<T> setParams(ApiParams params) {
        this.params = params;
        return this;
    }

    public RequestBuilder<T> putParameter(String key, String value) {
        if (params == null) {
            params = new ApiParams();
        }
        params.put(key, value);
        return this;
    }

    public RequestBuilder<T> putParameter(String key, int value) {
        return putParameter(key, String.valueOf(value));
    }

    public RequestBuilder<T> putParameter(String key, long value) {
        return putParameter(key, String.valueOf(value));
    }

    public RequestBuilder<T> putParameter(String key, float value) {
        return putParameter(key, String.valueOf(value));
    }

    public RequestBuilder<T> putParameter(String key, double value) {
        return putParameter(key, String.valueOf(value));
    }

    public RequestBuilder<T> putParameter(String key, File value) {
        if (params == null) {
            params = new ApiParams();
        }
        params.put(key, value);
        return this;
    }

    public RequestBuilder<T> putParameterIf(boolean add, String key, String value) {
        if (add) putParameter(key, value);
        return this;
    }
    //==================参数END======================


    public RequestBuilder<T> cacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
        return this;
    }

    public RequestBuilder<T> method(int method) {
        this.method = method;
        return this;
    }

    public RequestBuilder<T> cacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return this;
    }

    public RequestBuilder<T> retryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    public RequestBuilder<T> objectParser(ObjectParser<T> objectParser) {
        this.objectParser = objectParser;
        return this;
    }

    /**
     * 最大重试次数，若设置了retryPolicy已retryPolicy为主
     *
     * @param maxRetryCount 重试次数
     * @return
     */
    public RequestBuilder maxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
        return this;
    }

    /**
     * 超时时间，默认10*1000毫秒，若设置了retryPolicy已retryPolicy为主
     *
     * @param timeout 超时时间，单位毫秒
     * @return
     */
    public RequestBuilder<T> timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public ApiRequest<T> build() {
        if (method == GET && params != null) {
            if (url.contains("?")) {
                url = url + "&" + params.getParamString();
            } else {
                url = url + "?" + params.getParamString();
            }
        }
        if (timeout <= 20 * 1000) {
            timeout = 20 * 1000;
        }
        if (retryPolicy == null) {
            retryPolicy = new DefaultRetryPolicy(timeout, maxRetryCount, 1);
        }
        ApiRequest<T> request = new ApiRequest<T>(method, url, null);
        request.setShouldCache(shouldCache);
        request.setCacheKey(cacheKey);
        request.setCacheTime(cacheTime);
        request.setRequestParams(params);
        request.setObjectParser(objectParser);
        request.setRetryPolicy(retryPolicy);
        return request;
    }
}
