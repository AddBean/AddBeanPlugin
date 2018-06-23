package com.addbean.model;

import com.addbean.utils.core.BaseException;
import com.addbean.utils.http.HttpUtils;
import com.addbean.utils.http.OnHttpListener;
import io.netty.util.internal.StringUtil;

public class HttpHelper {

    /**
     * 翻译接口；
     *
     * @param word
     */
    public static void translate(String word, OnHttpListener<TranslateModel> onHttpListener) {
        if (word==null||word=="") {
            onHttpListener.onFailure(new BaseException("关键字不能为空！"));
            return;
        }
        HttpUtils.getInstance().get("http://fanyi.youdao.com/openapi.do?keyfrom=Skykai521&key=977124034&type=data&doctype=json&version=1.1&q=" + word, TranslateModel.class, onHttpListener);

    }
}
