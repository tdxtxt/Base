package com.pingerx.socialgo.qq.model

import com.pingerx.socialgo.core.model.user.BaseSocialUser


/**
 * 阿里登录用户的用户信息
 */
class AliUser : BaseSocialUser() {
    override fun getUserId(): String {
       return ""
    }

    override fun getUserNickName(): String {
        return ""
    }

    override fun getUserGender(): Int {
        return 0
    }

    override fun getUserProvince(): String {
        return ""
    }

    override fun getUserCity(): String {
        return ""
    }

    override fun getUserHeadUrl(): String {
        return ""
    }

    override fun getUserHeadUrlLarge(): String {
        return ""
    }

}
