package com.addbean.utils.http;


import com.addbean.utils.core.BaseException;
import com.google.gson.Gson;

import java.util.Map;

public class HttpUtils {
    public static HttpUtils HTTP;
    private final Gson gson;

    public HttpUtils() {
        gson=new Gson();
    }

    public static HttpUtils getInstance() {
        if (HTTP == null)
            HTTP = new HttpUtils();
        return HTTP;
    }

    public <T> void post(String url, final Map<String, String> map, final Class<T> classOfT, final OnHttpListener<T> onHttpListener) {
        HttpMate mate = HttpRequest.post(url, map);
        if (mate.getmType() == BaseException.ErrorType.SUCCESS) {
            try {
                if (classOfT.equals(String.class)) {
                    onHttpListener.onSuccess((T) mate.getmContent());
                    return;
                }
                System.out.print(mate.getmContent());
                T entity = gson.fromJson(mate.getmContent(), classOfT);
                onHttpListener.onSuccess(entity);
            } catch (Exception error) {
                onHttpListener.onFailure(new BaseException("Json转换出错"));
            }
        } else {
            onHttpListener.onFailure(new BaseException(mate.getmType()));
        }
    }

    public <T> void get(final String urlStr, final Class<T> classOfT, final OnHttpListener<T> onHttpListener) {
        HttpMate mate = HttpRequest.get(urlStr);
        if (mate.getmType() == BaseException.ErrorType.SUCCESS) {
            try {
                System.out.print(mate.getmContent());
                if (classOfT.equals(String.class)) {
                    onHttpListener.onSuccess((T) mate.getmContent());
                    return;
                }
                T entity = gson.fromJson(mate.getmContent(), classOfT);
                onHttpListener.onSuccess(entity);
            } catch (Exception error) {
                onHttpListener.onFailure(new BaseException("Json转换出错"));
            }
        } else {
            onHttpListener.onFailure(new BaseException(mate.getmType()));
        }
    }


}
