package com.addbean.utils.http;

import com.addbean.utils.core.BaseException;

/**
 * Created by Administrator on 2016/8/22.
 */
public class HttpMate {
    private String mContent;
    private BaseException.ErrorType mType;

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public BaseException.ErrorType getmType() {
        return mType;
    }

    public void setmType(BaseException.ErrorType mType) {
        this.mType = mType;
    }
}
