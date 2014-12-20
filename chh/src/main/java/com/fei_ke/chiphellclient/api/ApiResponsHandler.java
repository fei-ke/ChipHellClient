package com.fei_ke.chiphellclient.api;

import android.os.Message;

import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * 异步联网请求解析帮助类
 *
 * @param <T>
 * @author fei-ke
 *         2014-6-19 下午6:35:01
 */
public abstract class ApiResponsHandler<T> extends TextHttpResponseHandler {
    ApiCallBack<T> mApiCallBack;
    private int PARSED_MESSAGE = 10;
    private int PARSE_CACHE_MESSAGE = 11;

    public ApiResponsHandler(ApiCallBack<T> apiCallBack) {
        this.mApiCallBack = apiCallBack;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
        if (message.what == PARSED_MESSAGE) {
            if (mApiCallBack != null) {
                mApiCallBack.onSuccess((T) message.obj);
            }
        } else if (message.what == PARSE_CACHE_MESSAGE) {
            mApiCallBack.onCache((T) message.obj);
        }
    }

    @Override
    public void onStart() {
        if (mApiCallBack != null) {
            mApiCallBack.onStart();
        }
    }

    @Override
    public void onFinish() {
        if (mApiCallBack != null) {
            mApiCallBack.onFinish();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        if (mApiCallBack != null) {
            mApiCallBack.onFailure(throwable, responseString);
        }
    }

    @Override
    public void onProgress(int bytesWritten, int totalSize) {
        mApiCallBack.onProgress(bytesWritten, totalSize);
    }

    @Override
    // 后台线程解析
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        T t = parseResponse(responseString);

        sendMessage(obtainMessage(PARSED_MESSAGE, t));

    }

    @Override
    public void onCache(String cacheString) {
        T t = parseResponse(cacheString);
        sendMessage(obtainMessage(PARSE_CACHE_MESSAGE, t));
    }

    public abstract T parseResponse(String responseString);
}
