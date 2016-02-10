package com.fei_ke.chiphellclient.api.support;

import android.text.TextUtils;
import android.webkit.CookieManager;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fei on 16/2/1.
 */
public class WebViewCookieHandler extends CookieHandler {
    private CookieManager cookieManager = CookieManager.getInstance();

    @Override
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
        String url = uri.toString();
        String cookieValue = cookieManager.getCookie(url);
        Map<String, List<String>> cookies = new HashMap<>();
        if (!TextUtils.isEmpty(cookieValue)) {
            cookies.put("Cookie", Arrays.asList(cookieValue));
        }
        return cookies;
    }

    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
        String url = uri.toString();
        for (String header : responseHeaders.keySet()) {
            if (header.equalsIgnoreCase("Set-Cookie") || header.equalsIgnoreCase("Set-Cookie2")) {
                for (String value : responseHeaders.get(header)) {
                    //if (!TextUtils.isEmpty(value))
                    cookieManager.setCookie(url, value);
                }
            }
        }
    }
}
