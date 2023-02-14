package com.tdxtxt.net

import android.text.TextUtils
import com.google.gson.GsonBuilder
import com.tdxtxt.net.config.DefaultNetProvider
import com.tdxtxt.net.config.NetProvider
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/10
 *     desc   : 网络请求工具类
 * </pre>
 */
object NetMgr {
    private var providerMap = HashMap<String, NetProvider>(1)
    private var retrofitMap = HashMap<String, Retrofit>(1)
    private var clientMap = HashMap<String, OkHttpClient>(1)

    //必须注册调用此接口
    fun registerProvider(provider: NetProvider) {
        providerMap.put(provider.host(), provider)
    }

    fun getProvider(host: String): NetProvider{
        var provider = providerMap.get(host)
        if(provider == null){
            provider = object : DefaultNetProvider(){
                override fun host() = host
            }
            providerMap.put(host, provider)
        }
        return provider
    }

    //获取service服务
    fun <S> getService(host: String, service: Class<S>) = getRetrofit(host).create(service)

    fun clearCache(){
        retrofitMap.clear()
        clientMap.clear()
    }

    private fun getRetrofit(host: String) : Retrofit{
        if(checkBaseUrl(host)) throw  IllegalStateException("host error--> host=$host")
        var retrofit: Retrofit? = retrofitMap.get(host)
        if(retrofit != null) return retrofit

        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create()

        val builder = Retrofit.Builder()
                .baseUrl(host)
                .client(getClient(host, getProvider(host)))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))

        retrofit = builder.build()
        retrofitMap[host] = retrofit

        return retrofit
    }

    private fun getClient(host: String, provider: NetProvider): OkHttpClient{
        if(checkBaseUrl(host)) throw  IllegalStateException("baseUrl error--> baseurl=$host")

        if(clientMap.get(host) != null) return clientMap.get(host)!!

        val builder = OkHttpClient.Builder()
        provider.configHttpClient(builder)
        val client = provider.createOkHttpClient(builder)

        clientMap[host] = client
        return client
    }

    private fun checkBaseUrl(host: String) = TextUtils.isEmpty(host)

}