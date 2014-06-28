
package com.fei_ke.chiphellclient.api;

/**
 * 接口回调
 * 
 * @author fei-ke
 *         2014-6-10 上午9:33:24
 */
public abstract class ApiCallBack<T extends Object> {
    public void onSuccess(T result) {
    }

    public void onFailure(Throwable error, String content) {
    }

    public void onFinish() {
    }

    public void onStart() {
    }

    public void onCache(T result) {
    }

    public void onProgress(int bytesWritten, int totalSize) {

    }

}
