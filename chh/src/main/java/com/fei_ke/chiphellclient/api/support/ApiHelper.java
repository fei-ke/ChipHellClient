package com.fei_ke.chiphellclient.api.support;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fei_ke.chiphellclient.ChhApplication;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by fei on 15/11/21.
 */
public class ApiHelper {
    // TODO: 15/11/21 queue
    static {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setCookieHandler(new WebViewCookieHandler());
        OkHttpStack stack = new OkHttpStack(new OkUrlFactory(okHttpClient));
        requestQueue = Volley.newRequestQueue(ChhApplication.getInstance(), stack);
    }

    private static RequestQueue requestQueue;

    /**
     * 请求接口并回调
     *
     * @param request  请求
     * @param callBack 回调
     * @param <T>      响应数据类型
     */
    public static <T> void requestApi(ApiRequest<T> request, ApiCallBack<T> callBack) {
        request.setCallback(callBack);
        requestQueue.add(request);
    }

    /**
     * 请求接口不进行回调
     *
     * @param request 请求
     * @param <T>     响应数据类型
     */
    public static <T> void requestApi(ApiRequest<T> request) {
        requestQueue.add(request);
    }

    /**
     * 同步请求接口
     *
     * @param request 请求
     * @param <T>     解析类型
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public static <T> T requestSyncApi(ApiRequest<T> request) throws InterruptedException, ExecutionException, TimeoutException {
        ApiFuture<T> apiFuture = ApiFuture.newFuture();
        request.setCallback(apiFuture);
        requestQueue.add(request);
        return apiFuture.get(request.getTimeoutMs(), TimeUnit.MILLISECONDS);
    }

    /**
     * 取消所有请求
     */
    public static void cancelAll() {
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    /**
     * 取消所有标记为tag的请求
     *
     * @param tag
     */
    public static void cancelAll(Object tag) {
        requestQueue.cancelAll(tag);
    }
}
