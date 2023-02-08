package com.pingerx.socialgo.core.listener;

import com.pingerx.socialgo.core.exception.SocialError;

public abstract class OnPayJavaListener {
    public void onStart(){};

    public abstract void onSuccess();

    public abstract void onCancel();

    public abstract void onFailure(SocialError error);

    public void onDealing(){}

    public void printLog(String msg){}
}
