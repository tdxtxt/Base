package com.tdxtxt.net.config

import android.util.Log
import com.tdxtxt.net.log.HttpLogger
import com.tdxtxt.net.model.AbsResponse
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import retrofit2.HttpException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLException

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/10
 *     desc   : 网络请求配置默认类
 * </pre>
 */
abstract class DefaultNetProvider : NetProvider {
    private var CONNECT_TIME_OUT: Long = 30
    private var READ_TIME_OUT: Long = 180
    private var WRITE_TIME_OUT: Long = 30

    override fun handleError(response: AbsResponse?, errorCode: Int?, errorMsg: String?) {

    }

    override fun throwable2Message(e: Throwable?): String {
        if(e == null) return "未知错误"
        return when(e){
            is UnknownHostException -> "网络异常"
            is SocketTimeoutException -> "请求网络超时"
            is SocketException -> "网络异常"
            is SSLException -> "网络异常"
            is JSONException -> "数据解析错误"
            else -> "请求失败，请重试"
        }
    }

    override fun throwable2Code(e: Throwable?): Int {
        if(e is HttpException) return e.code()
        return -888
    }

    override fun throwable2Response(e: Throwable?): AbsResponse? {
//        when(e){
//            is HttpException ->{
//                val errorBody = e.response().errorBody()
//                val json = errorBody?.string()
//                return Gson().fromJson(json, T::class.java)
//            }
//            else -> return null
//        }
        return null
    }

    override fun throwable2ErrorBody(e: Throwable?): String? {
        when (e) {
            is HttpException -> {
                val errorBody = e.response().errorBody()
                val json = errorBody?.string()
                return json
            }
            else -> return null
        }
    }

    override fun createOkHttpClient(builder: OkHttpClient.Builder): OkHttpClient {
        return builder.build()
    }

    override fun configHttpClient(builder: OkHttpClient.Builder) {
        val loggingInterceptor = HttpLoggingInterceptor(HttpLogger(host()))
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(NetInterceptor(getRequestHandler()))
            .addInterceptor(loggingInterceptor)
            .protocols(Collections.unmodifiableList(listOf(Protocol.HTTP_1_1, Protocol.HTTP_2)))
    }

    override fun getRequestHandler(): RequestHandler? {
        return null
    }

    override fun printLog(message: String) {
        Log.i("http::tdxtxt", message)
    }

}