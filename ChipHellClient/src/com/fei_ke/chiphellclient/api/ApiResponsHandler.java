
package com.fei_ke.chiphellclient.api;

import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

public abstract class ApiResponsHandler<T> extends TextHttpResponseHandler {
    ApiCallBack<T> mApiCallBack;

    public ApiResponsHandler(ApiCallBack<T> apiCallBack) {
        this.mApiCallBack = apiCallBack;
    }

    @Override
    public void onStart() {
        mApiCallBack.onStart();
    }

    @Override
    public void onFinish() {
        mApiCallBack.onFinish();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        mApiCallBack.onFailure(throwable, responseString);
    }

    @Override
    // TODO 放到后台线程解析
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        System.out.println("satatusCode " + statusCode);
        T t = onSuccessThenParse(responseString);
        mApiCallBack.onSuccess(t);
    }

    public abstract T onSuccessThenParse(String responseString);
}
