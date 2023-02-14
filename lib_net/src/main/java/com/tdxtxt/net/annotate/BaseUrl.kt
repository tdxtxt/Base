package com.tdxtxt.net.annotate

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/14
 *     desc   :
 * </pre>
 */
@kotlin.annotation.Target(AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class BaseUrl constructor(val host: String)