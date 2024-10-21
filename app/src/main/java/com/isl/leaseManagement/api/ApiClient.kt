package com.isl.leaseManagement.api

import ApiInterceptor
import IApiRequest
import com.google.gson.GsonBuilder
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.utils.AppConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {  //this class is  for creating an instance which will call apis

    var request: IApiRequest? = null

    fun init() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(ApiInterceptor())
            .addInterceptor(interceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()


        val retrofit = Retrofit.Builder()
            .baseUrl(AppConstants.baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls().create()
                )
            )
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        request = retrofit.create(IApiRequest::class.java)
    }

    fun handleApiError(e: Throwable, defaultErrorMessage: String, baseActivity: BaseActivity) {
        if (e is HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                if (!errorBody.isNullOrEmpty()) {
                    val jsonError = JSONObject(errorBody)
                    val errorMessage = jsonError.getJSONObject("error").getString("message")
                    baseActivity.showToastMessage(errorMessage)
                } else {
                    baseActivity.showToastMessage(defaultErrorMessage)
                }
            } catch (jsonException: Exception) {
                baseActivity.showToastMessage(defaultErrorMessage)
            }
        } else {
            baseActivity.showToastMessage(defaultErrorMessage)
        }
    }


}