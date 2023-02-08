package com.pingerx.socialgo.core.listener;

import com.pingerx.socialgo.core.exception.SocialError;

public abstract class OnShareJavaListener {
    public void onStart(){}

    public void onSuccess(){}

    public void onFailure(SocialError error){}

    public void onCancel(){}
}
