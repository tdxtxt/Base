package com.tdxtxt.baselib.tools

import com.tdxtxt.baselib.storage.CEngine
import com.tdxtxt.baselib.storage.MMKVEngine

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/13
 *     desc   :
 * </pre>
 */
object CacheHelper : CEngine by object : MMKVEngine() {
    override fun createMMKVFileKey() = "CommKey"
}{
    fun isAgreePrivacy() = getBoolean("isAgreePrivacy", false)

    fun setAgreePrivacy(isAgree: Boolean){
        putBoolean("isAgreePrivacy", isAgree)
    }
}