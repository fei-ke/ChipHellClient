package com.fei_ke.chiphellclient.utils;

import java.util.HashMap;

/**
 * 将url中的请求参数转换为map
 *
 * @author fei-ke
 * @2014-6-19
 */
public class UrlParamsMap extends HashMap<String, String> {
    public UrlParamsMap(String url) {

        int s = url.indexOf("?");
        if (s == -1)
            return;
        url = url.substring(s + 1);

        String[] params = url.split("&");
        for (String paramGroup : params) {
            String[] param = paramGroup.split("=");
            String key = param[0];
            String value;
            if (param.length > 1) {
                value = param[1];
            } else {
                value = "";
            }
            put(key, value);
        }
    }
}
