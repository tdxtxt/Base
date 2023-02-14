package com.tdxtxt.base.net.data

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/13
 *     desc   :
 * </pre>
 */
data class BaseListBody <T> constructor(val pageNum: Int, val pageSize: Int, val totalPage: Int, val totalCount: Int, val list: List<T>?) {

}