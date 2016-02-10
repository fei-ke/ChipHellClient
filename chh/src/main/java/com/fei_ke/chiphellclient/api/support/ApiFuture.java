package com.fei_ke.chiphellclient.api.support;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 用来同步回调
 * Created by 杨金阳 on 2015/11/19.
 */
public class ApiFuture<T> extends ApiCallBack<T> implements Future<T> {
    private Request<?> mRequest;
    private boolean mResultReceived = false;
    private T mResult;
    private VolleyError mException;
    private final Object lock = new Object();

    public static <E> ApiFuture<E> newFuture() {
        return new ApiFuture<E>(){};
    }

    private ApiFuture() {}

    public void setRequest(Request<?> request) {
        mRequest = request;
    }

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (mRequest == null) {
            return false;
        }

        if (!isDone()) {
            mRequest.cancel();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        try {
            return doGet(null);
        } catch (TimeoutException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return doGet(TimeUnit.MILLISECONDS.convert(timeout, unit));
    }

    private synchronized T doGet(Long timeoutMs)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (mException != null) {
            throw new ExecutionException(mException);
        }

        if (mResultReceived) {
            return mResult;
        }
        synchronized (lock) {
            if (timeoutMs == null) {
                lock.wait(0);
            } else if (timeoutMs > 0) {
                lock.wait(timeoutMs);
            }
        }
        if (mException != null) {
            throw new ExecutionException(mException);
        }

        if (!mResultReceived) {
            throw new TimeoutException();
        }

        return mResult;
    }

    @Override
    public boolean isCancelled() {
        if (mRequest == null) {
            return false;
        }
        return mRequest.isCanceled();
    }

    @Override
    public synchronized boolean isDone() {
        return mResultReceived || mException != null || isCancelled();
    }

    @Override
    public void onFailure(Throwable error, String content) {
        mException = new VolleyError(content, error);
    }

    @Override
    public void onSuccess(T result) {
        mResultReceived = true;
        mResult = result;
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
