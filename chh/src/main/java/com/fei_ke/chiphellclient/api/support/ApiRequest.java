package com.fei_ke.chiphellclient.api.support;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fei_ke.chiphellclient.BuildConfig;
import com.fei_ke.chiphellclient.analytics.Analytics;
import com.fei_ke.chiphellclient.utils.LogMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * API请求
 * Created by 杨金阳 on 2015/7/28.
 */
public class ApiRequest<T> extends Request<T> {
    private static final String TAG = "ApiRequest";

    private ApiCallBack<T> mCallback;
    private RequestParams mRequestParams;
    private String cacheKey;
    private long cacheTime;
    private Priority mPriority;
    private AtomicBoolean isResponseFromCache = new AtomicBoolean(false);
    private ObjectParser<T> objectParser;

    public ApiRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }

    @Deprecated
    public ApiRequest(String url, Response.ErrorListener listener) {
        super(url, listener);
    }

    public void setCallback(ApiCallBack<T> callback) {
        mCallback = callback;
    }

    public void setRequestParams(RequestParams requestParams) {
        mRequestParams = requestParams;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public void setObjectParser(ObjectParser<T> objectParser) {
        this.objectParser = objectParser;
    }

    @Override
    public Priority getPriority() {
        if (mPriority != null) {
            return mPriority;
        } else {
            return super.getPriority();
        }
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        int method = getMethod();
        if (method == Method.POST || method == Method.PUT || method == Method.PATCH) {
            return mRequestParams == null ? null : mRequestParams.getPostBody();
        } else {
            return null;
        }
    }

    @Override
    public String getBodyContentType() {
        if (mRequestParams == null || mRequestParams.getContentType() == null) {
            return super.getBodyContentType();
        } else {
            return mRequestParams.getContentType();
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String responseJson;
        try {
            responseJson = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            responseJson = new String(response.data);
        }
        Cache.Entry cacheEntry = null;
        try {
            cacheEntry = handleCache(response);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //debug状态打印请求日志
        if (BuildConfig.DEBUG) {
            logRequest(response, responseJson);
        }

        try {
            T resultObject = parseObject(responseJson);
            return Response.success(resultObject, cacheEntry);
        } catch (VolleyError e) {
            return Response.error(new VolleyError(responseJson, e));
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return Response.error(new VolleyError(e));
        }
    }

    private void logRequest(NetworkResponse response, String responseJson) {
        int method = getMethod();
        String url = null;
        if ((method == Method.POST || method == Method.PUT || method == Method.PATCH)
                && mRequestParams != null) {
            url = getUrl() + "?" + mRequestParams.getParamString();
        } else {
            url = getUrl();
        }

        String message = null;
        try {
            if (responseJson.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(responseJson);
                message = jsonObject.toString(4);
            } else if (responseJson.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(responseJson);
                message = jsonArray.toString(4);
            } else {
                message = responseJson;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            message = responseJson;
        }


        Log.d(TAG, "╔════════════════════════════════════════════");
        Log.d(TAG, "║URL: " + url);
        Log.d(TAG, "╟────────────────────────────────────────────");
        Log.d(TAG, "║METHOD: " + getMethodName(getMethod()));
        Log.d(TAG, "╟────────────────────────────────────────────");
        Log.d(TAG, "║TIME: " + response.networkTimeMs);
        Log.d(TAG, "╟────────────────────────────────────────────");
        Log.d(TAG, "║FROM_CACHE: " + isResponseFromCache.get());
        Log.d(TAG, "╟────────────────────────────────────────────");
        Log.d(TAG, "║CACHE_KEY: " + getCacheKey());
        Log.d(TAG, "╟────────────────────────────────────────────");
        Log.v(TAG, "║RESPONSE: \n" + message);
        Log.d(TAG, "╚════════════════════════════════════════════");

    }

    private static String getMethodName(int method) {
        switch (method) {
            case Request.Method.DEPRECATED_GET_OR_POST:
                return "DEPRECATED_GET_OR_POST";
            case Request.Method.GET:
                return "GET";
            case Request.Method.POST:
                return "POST";
            case Request.Method.PUT:
                return "PUT";
            case Request.Method.DELETE:
                return "DELETE";
            case Request.Method.HEAD:
                return "HEAD";
            case Request.Method.OPTIONS:
                return "OPTIONS";
            case Request.Method.TRACE:
                return "TRACE";
            case Request.Method.PATCH:
                return "PATCH";
        }
        return null;
    }

    private Cache.Entry handleCache(NetworkResponse response) {
        //通过联网时间判断response是否来自缓存
        boolean fromCache = response.networkTimeMs == 0;
        isResponseFromCache.set(fromCache);
        //如果来自缓存||没有请求缓存 则不进行缓存
        if (fromCache || !shouldCache()) return null;

        //来自真实的请求相应，进行缓存
        Cache.Entry cacheEntry = new Cache.Entry();
        cacheEntry.data = response.data;
        cacheEntry.responseHeaders = response.headers;
        if (cacheTime != 0) {
            cacheEntry.ttl = System.currentTimeMillis() + cacheTime;
        } else {
            cacheEntry.ttl = Long.MAX_VALUE;
        }
        //cacheEntry.softTtl = System.currentTimeMillis() + 1000;
        return cacheEntry;
    }

    @Override
    public Request<?> setRequestQueue(RequestQueue requestQueue) {
        onStart();
        return super.setRequestQueue(requestQueue);
    }

    @Override
    protected void deliverResponse(T response) {
        if (shouldCache() && isResponseFromCache.get()) {
            onCache(response);
        } else {
            onSuccess(response);
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        onCancel();
    }

    @Override
    public void deliverError(VolleyError error) {
        onFailure(error, error.getMessage());
    }

    protected void onStart() {
        if (LogMessage.isDebug()) {
            try {
                int method = getMethod();
                if (method == Method.POST || method == Method.PUT || method == Method.PATCH) {
                    LogMessage.i(TAG, getUrl() + "?" + new String(getBody()));
                } else {
                    LogMessage.i(TAG, getUrl());
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (mCallback != null) {
            mCallback.onStart();
        }
    }

    protected void onSuccess(T result) {
        if (mCallback != null) {
            //线上状态catch异常
            if (!BuildConfig.DEBUG) {
                try {
                    mCallback.onSuccess(result);
                } catch (Exception e) {
                    mCallback.onFailure(new NullPointerException("出错啦，请稍后重试"), "出错啦，请稍后重试");
                    Analytics.reportError(e);
                }
            } else {
                mCallback.onSuccess(result);
            }

        }
    }


    protected void onCache(T result) {
        if (mCallback != null) {
            //线上状态catch异常
            if (!BuildConfig.DEBUG) {
                try {
                    mCallback.onCache(result);
                } catch (Exception e) {
                    Analytics.reportError(e);
                }
            } else {
                mCallback.onCache(result);
            }
        }
    }

    protected void onFailure(Throwable error, String content) {
        if (mCallback != null) {
            mCallback.onFailure(error, content);
        }
    }

    protected void onCancel() {
        if (mCallback != null) {
            mCallback.onCancel();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (mCallback != null) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                Handler mainThread = new Handler(Looper.getMainLooper());
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onFinish();
                    }
                });
            } else {
                mCallback.onFinish();
            }
        }
    }


    @Override
    public String getCacheKey() {
        if (!TextUtils.isEmpty(cacheKey)) {
            return cacheKey;
        }
        return super.getCacheKey();
    }


    protected T parseObject(String content) throws Exception {
        if (objectParser != null) {
            return objectParser.parse(content);
        } else {
            return null;
        }
    }
}
