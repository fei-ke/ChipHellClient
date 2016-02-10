package com.fei_ke.chiphellclient.api.support;



import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 请求参数
 * Created by 杨金阳 on 2015/8/13.
 */
public class ApiParams implements RequestParams {
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    protected final Map<String, String> params = new TreeMap<String, String>();
    protected Map<String, File> fileParams;
    private String bodyContentType;

    public ApiParams put(String key, String value) {
        params.put(key, String.valueOf(value));
        return this;
    }

    public ApiParams put(String key, long value) {
        return put(key, String.valueOf(value));
    }

    public ApiParams put(String key, double value) {
        return put(key, String.valueOf(value));
    }

    public ApiParams put(String key, File value) {
        if (fileParams == null) {
            fileParams = new HashMap<>();
        }
        fileParams.put(key, value);
        return this;
    }

    public ApiParams put(String key, InputStream value) {
        throw new UnsupportedOperationException("not support yet");
    }

    public ApiParams put(String key, int value) {
        return put(key, String.valueOf(value));
    }


    public ApiParams put(String key, float value) {
        return put(key, String.valueOf(value));
    }

    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public byte[] getPostBody() {

        if (fileParams != null) {
            return getMultipartBody(DEFAULT_PARAMS_ENCODING);
        } else {
            return getPostBody(DEFAULT_PARAMS_ENCODING);
        }
    }

    private byte[] getMultipartBody(String paramsEncoding) {
        //MultipartEntity entity = new MultipartEntity();
        //try {
        //    for (Map.Entry<String, String> entry : params.entrySet()) {
        //        entity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName(paramsEncoding)));
        //    }
        //    for (Map.Entry<String, File> entry : fileParams.entrySet()) {
        //        entity.addPart(entry.getKey(), new FileBody(entry.getValue()));
        //    }
        //} catch (UnsupportedEncodingException e) {
        //    e.printStackTrace();
        //}
        //for (Map.Entry<String, File> entry : fileParams.entrySet()) {
        //    entity.addPart(entry.getKey(), new FileBody(entry.getValue()));
        //}
        //
        //bodyContentType = entity.getContentType().getValue();
        //
        //ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //try {
        //    entity.writeTo(bos);
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        //return bos.toByteArray();
        return null;
    }

    public byte[] getPostBody(String paramsEncoding) {
        try {
            return getParamString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }



    @Override
    public String getParamString() {
        return getParamString(DEFAULT_PARAMS_ENCODING);
    }

    @Override
    public String getContentType() {
        return bodyContentType;
    }


    private String getParamString(String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            if (encodedParams.length() != 0) {
                encodedParams.deleteCharAt(encodedParams.length() - 1);
            }
            return encodedParams.toString();
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }


}
