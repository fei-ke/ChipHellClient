/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package com.loopj.android.http;

import android.util.Log;

import org.afinal.simplecache.ACache;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

/**
 * Internal class, representing the HttpRequest, done in asynchronous manner
 */
public class AsyncHttpRequest implements Runnable {
    private final AbstractHttpClient client;
    private final HttpContext context;
    private final HttpUriRequest request;
    private final ResponseHandlerInterface responseHandler;
    private int executionCount;
    private boolean isCancelled = false;
    private boolean cancelIsNotified = false;
    private boolean isFinished = false;

    //缓存相关
    private boolean isCacheAble;
    private int cacheTime=0;//保存时间，单位秒,默认0永久保存
    private File cacheDir;//缓存目录

    public AsyncHttpRequest(AbstractHttpClient client, HttpContext context, HttpUriRequest request,
            ResponseHandlerInterface responseHandler, boolean isCacheAble,File cacheDir,int cacheTime) {
        this.client = client;
        this.context = context;
        this.request = request;
        this.responseHandler = responseHandler;
        
        this.isCacheAble=isCacheAble;
        this.cacheDir=cacheDir;
        this.cacheTime=cacheTime;
        
    }

    @Override
    public void run() {
        if (isCancelled()) {
            return;
        }

        if (responseHandler != null) {
            responseHandler.sendStartMessage();
        }

        if (isCancelled()) {
            return;
        }

        try {
            makeRequestWithRetries();
        } catch (IOException e) {
            if (!isCancelled() && responseHandler != null) {
                responseHandler.sendFailureMessage(0, null, null, e);
            } else {
                Log.e("AsyncHttpRequest", "makeRequestWithRetries returned error, but handler is null", e);
            }
        }

        if (isCancelled()) {
            return;
        }

        if (responseHandler != null) {
            responseHandler.sendFinishMessage();
        }

        isFinished = true;
    }

    private void makeRequest() throws IOException {
        if (isCancelled()) {
            return;
        }
        // Fixes #115
        if (request.getURI().getScheme() == null) {
            // subclass of IOException so processed in the caller
            throw new MalformedURLException("No valid URI scheme was provided");
        }
        // 读取缓存
        if(isCacheAble) {
            ACache aCache=ACache.get(cacheDir);
            String key=request.getURI().toString();
            if(BuildConfig.DEBUG){
                Log.i("读缓存 key:", ""+key);
            }
            byte[] cache = aCache.getAsBinary(key);
            if(cache!=null) {
                responseHandler.sendCacheMessage(cache);
            }
        }

        HttpResponse response = client.execute(request, context);
        // 写缓存
        if (!isCancelled() && responseHandler != null) {
            byte[] cache = responseHandler.sendResponseMessage(response);
            if (isCacheAble && cache != null) {
                ACache aCache = ACache.get(cacheDir);
                String key = request.getURI().toString();
                if (BuildConfig.DEBUG) {
                    Log.i("写缓存 key:", "" + key);
                }
                if (cacheTime == 0) {
                    aCache.put(key, cache);
                } else {
                    aCache.put(key, cache, cacheTime);
                }
            }
        }
    }

    private void makeRequestWithRetries() throws IOException {
        boolean retry = true;
        IOException cause = null;
        HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
        try {
            while (retry) {
                try {
                    makeRequest();
                    return;
                } catch (UnknownHostException e) {
                    // switching between WI-FI and mobile data networks can cause a retry which then results in an UnknownHostException
                    // while the WI-FI is initialising. The retry logic will be invoked here, if this is NOT the first retry
                    // (to assist in genuine cases of unknown host) which seems better than outright failure
                    cause = new IOException("UnknownHostException exception: " + e.getMessage());
                    retry = (executionCount > 0) && retryHandler.retryRequest(cause, ++executionCount, context);
                } catch (NullPointerException e) {
                    // there's a bug in HttpClient 4.0.x that on some occasions causes
                    // DefaultRequestExecutor to throw an NPE, see
                    // http://code.google.com/p/android/issues/detail?id=5255
                    cause = new IOException("NPE in HttpClient: " + e.getMessage());
                    retry = retryHandler.retryRequest(cause, ++executionCount, context);
                } catch (IOException e) {
                    if (isCancelled()) {
                        // Eating exception, as the request was cancelled
                        return;
                    }
                    cause = e;
                    retry = retryHandler.retryRequest(cause, ++executionCount, context);
                }
                if (retry && (responseHandler != null)) {
                    responseHandler.sendRetryMessage(executionCount);
                }
            }
        } catch (Exception e) {
            // catch anything else to ensure failure message is propagated
            Log.e("AsyncHttpRequest", "Unhandled exception origin cause", e);
            cause = new IOException("Unhandled exception: " + e.getMessage());
        }

        // cleaned up to throw IOException
        throw (cause);
    }

    public boolean isCancelled() {
        if (isCancelled) {
            sendCancelNotification();
        }
        return isCancelled;
    }

    private synchronized void sendCancelNotification() {
        if (!isFinished && isCancelled && !cancelIsNotified) {
            cancelIsNotified = true;
            if (responseHandler != null)
                responseHandler.sendCancelMessage();
        }
    }

    public boolean isDone() {
        return isCancelled() || isFinished;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        isCancelled = true;
        request.abort();
        return isCancelled();
    }
}
