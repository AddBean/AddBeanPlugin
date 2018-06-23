package com.addbean.utils.core;


/**
 * Created by AddBean on 2016/5/12.
 */
public class BaseException extends Exception {
    private ErrorType mErrorType;
    private String mMsg;

    public BaseException(String msg) {
        super(msg);
        this.mMsg = msg;
    }

    public BaseException(ErrorType type) {
        this.mErrorType = type;
    }

    public String getMessage() {
        if (mErrorType != null)
            return mErrorType.mMsg;
        return mMsg;
    }

    public enum ErrorType {
        NET_UNAVAILABLE(-1, "网络异常"),
        SERVER_UNAVAILABLE(404, "服务器异常"),
        SUCCESS(200, "请求成功!"),
        URL_FORMAT_ERROR(1, "URL格式异常"),
        STREAM_ERROR(2, "Stream异常");
        public String mMsg;
        public int mCode;

        ErrorType(int code, String msg) {
            this.mCode = code;
            this.mMsg = msg;
        }

    }
}
