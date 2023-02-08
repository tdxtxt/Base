package com.pingerx.socialgo.weibo.model

import com.pingerx.socialgo.core.model.token.AccessToken
import com.pingerx.socialgo.core.platform.Target
import com.sina.weibo.sdk.auth.Oauth2AccessToken

/**
 * 新浪的token
 */
class SinaAccessToken(token: Oauth2AccessToken) : AccessToken() {

    private val refresh_token: String
    private val phone: String? = ""

    init {
        this.openid = token.uid
        this.access_token = token.accessToken
        this.expires_in = token.expiresTime
        this.refresh_token = token.refreshToken
//        this.phone = token.getphoneNum
    }

    override fun loginTarget(): Int {
        return Target.LOGIN_WB
    }
}
