package com.tdxtxt.baselib.storage

import android.content.Context
import android.os.Parcelable
import com.blankj.utilcode.util.GsonUtils
import com.tencent.mmkv.MMKV

/**
 * 功能描述:
 * @author tangdexiang
 * @since 2021/12/11
 */
abstract class MMKVEngine : CEngine {
    abstract fun createMMKVFileKey(): String
    
    override fun init(context: Context) {
        val rootDir = MMKV.initialize(context)
    }

    override fun putString(key: String, value: String?) = MMKV.mmkvWithID(createMMKVFileKey()).encode(key, value)

    override fun putInt(key: String, value: Int) = MMKV.mmkvWithID(createMMKVFileKey()).encode(key, value)

    override fun putLong(key: String, value: Long) = MMKV.mmkvWithID(createMMKVFileKey()).encode(key, value)

    override fun putFloat(key: String, value: Float) = MMKV.mmkvWithID(createMMKVFileKey()).encode(key, value)

    override fun putDouble(key: String, value: Double) = MMKV.mmkvWithID(createMMKVFileKey()).encode(key, value)

    override fun putBoolean(key: String, value: Boolean) = MMKV.mmkvWithID(createMMKVFileKey()).encode(key, value)

    override fun putParcelable(key: String, value: Parcelable?) = MMKV.mmkvWithID(createMMKVFileKey()).encode(key, value)

    override fun putJson(key: String, value: Any?) =  putString(key, GsonUtils.toJson(value))

    override fun getString(key: String, defaultValue: String) = MMKV.mmkvWithID(createMMKVFileKey()).decodeString(key, defaultValue)

    override fun getInt(key: String, defaultValue: Int) = MMKV.mmkvWithID(createMMKVFileKey()).decodeInt(key, defaultValue)

    override fun getLong(key: String, defaultValue: Long) = MMKV.mmkvWithID(createMMKVFileKey()).decodeLong(key, defaultValue)

    override fun getFloat(key: String, defaultValue: Float)  = MMKV.mmkvWithID(createMMKVFileKey()).decodeFloat(key, defaultValue)

    override fun getDouble(key: String, defaultValue: Double) = MMKV.mmkvWithID(createMMKVFileKey()).decodeDouble(key, defaultValue)

    override fun getBoolean(key: String, defaultValue: Boolean) = MMKV.mmkvWithID(createMMKVFileKey()).decodeBool(key, defaultValue)

    override fun <T : Parcelable> getParcelable(key: String, clazz: Class<T>, defaultVale: T?) = MMKV.mmkvWithID(createMMKVFileKey()).decodeParcelable(key, clazz, defaultVale)

    override fun <T> getJson(key: String, clazz: Class<T>, defaultVale: T?): T? {
        val json = getString(key)
        return GsonUtils.fromJson(json, clazz)
    }

    override fun contains(key: String): Boolean? = MMKV.mmkvWithID(createMMKVFileKey()).containsKey(key)

    override fun getAllKeys(): List<String>? = MMKV.mmkvWithID(createMMKVFileKey()).allKeys()?.toList()

    override fun removeAll() = MMKV.mmkvWithID(createMMKVFileKey()).clearAll().run { true }

    override fun remove(key: String) = MMKV.mmkvWithID(createMMKVFileKey()).remove(key).run { true }
}