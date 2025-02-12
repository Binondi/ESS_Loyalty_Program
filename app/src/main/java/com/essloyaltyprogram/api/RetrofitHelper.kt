package com.essloyaltyprogram.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap


object RetrofitHelper {
    private val retrofitInstances: MutableMap<String, Retrofit> = HashMap()

    fun getInstance(baseUrl: String): Retrofit? {
        if (!retrofitInstances.containsKey(baseUrl)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofitInstances[baseUrl] = retrofit
        }
        return retrofitInstances[baseUrl]
    }

    fun <T> createService(baseUrl: String, serviceClass: Class<T>?): T {
        return getInstance(baseUrl)!!.create(serviceClass)
    }

    private val fast2SmsInstances: MutableMap<String, Retrofit> = ConcurrentHashMap()

    fun getFast2SmsInstance(baseUrl: String, apiKey: String): Retrofit {
        return fast2SmsInstances.getOrPut(apiKey) {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain: Interceptor.Chain ->
                    val request: Request = chain.request().newBuilder()
                        .addHeader("authorization", apiKey)
                        .addHeader("Content-Type", "application/json")
                        .build()
                    chain.proceed(request)
                }
                .build()

            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }


    fun <T> createOtpService(baseUrl: String, apiKey: String, serviceClass: Class<T>): T {
        return getFast2SmsInstance(baseUrl, apiKey).create(serviceClass)
    }
}
