package com.fei_ke.chiphellclient.api.support;


/**
 * 接口异步回调
 *
 * @param <T>
 * @author 杨金阳
 *         2014年6月27日 下午2:22:33
 */
public abstract class ApiCallBack<T> {
    public void onSuccess(T result) { }

    public void onFailure(Throwable error, String content) { }

    public void onFinish() { }

    public void onStart() { }

    public void onCache(T result) { }

    void onCancel() { }

}
