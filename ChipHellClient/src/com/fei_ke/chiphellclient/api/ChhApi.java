
package com.fei_ke.chiphellclient.api;

import com.fei_ke.chiphellclient.constant.Mode;
import com.loopj.android.http.AsyncHttpClient;

/**
 * CHH论坛接口
 */
public class ChhApi {
    private AsyncHttpClient mAsyncHttpClient;
    private Mode mMode;

    public ChhApi() {
        this(Mode.MOBILE);
    }

    public ChhApi(Mode mode) {
        this.mMode = mode;
    }

    /**
     * 获取版块列表
     */
    public void getPlateGroups() {
        
    }

    private AsyncHttpClient getAsyncHttpClient() {
        if (mAsyncHttpClient == null) {
            mAsyncHttpClient = new AsyncHttpClient();
        }
        return mAsyncHttpClient;
    }
}
