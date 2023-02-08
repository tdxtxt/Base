package com.pingerx.socialgo.core.listener;

import com.pingerx.socialgo.core.exception.SocialError;
import com.pingerx.socialgo.core.model.LoginResult;

public class OnLoginJavaListener {
    public void onStart(){}

    public void onSuccess(LoginResult result){}

    public void onFailure(SocialError error){}

    public void onCancel(){}
}
