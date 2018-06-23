package com.addbean.utils.http;

/**
 * Created by Administrator.
 */

public abstract class OnHttpListener<T> {
    public abstract void onSuccess(T data);

    public void onFailure(Exception e) {

    }
}
