package com.pingerx.socialgo.qq.model

import com.pingerx.socialgo.core.model.token.AccessToken
import com.pingerx.socialgo.core.platform.Target


/**
 * 支付宝授权登录的AccessToken
 */
class AliAccessToken : AccessToken() {
    override fun loginTarget(): Int {
        return Target.LOGIN_ALI
    }
}
