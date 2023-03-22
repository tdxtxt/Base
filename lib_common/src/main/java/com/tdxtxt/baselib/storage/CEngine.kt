package com.tdxtxt.baselib.storage

import android.content.Context
import android.os.Parcelable

/**
 * 创建时间： 2020/5/22
 * 编码： tangdex
 * 功能描述: 缓存工具类抽象方法
 */
interface CEngine {
    fun init(context: Context)

    fun putString(key: String, value: String?): Boolean?
    fun putInt(key: String, value: Int): Boolean?
    fun putLong(key: String, value: Long): Boolean?
    fun putFloat(key: String, value: Float): Boolean?
    fun putDouble(key: String, value: Double): Boolean?
    fun putBoolean(key: String, value: Boolean): Boolean?
    fun putParcelable(key: String, value: Parcelable?): Boolean?
    fun putJson(key : String, value: Any?): Boolean?

    fun getString(key: String, defaultValue: String = ""): String?
    fun getInt(key: String, defaultValue: Int = 0): Int
    fun getLong(key: String, defaultValue: Long = 0): Long
    fun getFloat(key: String, defaultValue: Float = 0F): Float
    fun getDouble(key: String, defaultValue: Double = 0.0): Double
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    fun <T : Parcelable> getParcelable(key: String, clazz: Class<T>, defaultVale: T? = null): T?
    fun <T> getJson(key: String, clazz: Class<T>, defaultVale: T? = null): T?

    fun contains(key: String): Boolean?

    fun getAllKeys(): List<String>?

    fun removeAll(): Boolean?
    fun remove(key: String): Boolean?

}