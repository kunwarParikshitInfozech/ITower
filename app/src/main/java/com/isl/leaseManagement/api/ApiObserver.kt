package com.isl.leaseManagement.api

import com.google.gson.Gson
import com.isl.leaseManagement.base.ErrorResponse
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

abstract class ApiObserver<T> : Observer<Response<T>> {   //this class is used for observing success/failure response
    override fun onSubscribe(d: Disposable) {
        // Show Dialog
    }

    override fun onNext(response: Response<T>) {
        if (response.isSuccessful) {
            onSuccess(response.body())
        } else {
            val errorBody = response.errorBody()
            val statusCode = response.code()
            handleError(statusCode, errorBody)
        }
    }

    override fun onError(e: Throwable) {
        var body: ErrorResponse? = null
        if (e is HttpException) {
            try {
                body = e.response()?.errorBody()?.string()
                    ?.let { Gson().fromJson(it, ErrorResponse::class.java) }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        onError(body)
    }

    override fun onComplete() {
        // Hide dialog
    }

    abstract fun onSuccess(response: T?)

    abstract fun onError(error: ErrorResponse?)

    private fun handleError(statusCode: Int, errorBody: ResponseBody?) {
        var body: ErrorResponse? = null
        if (errorBody != null) {
            try {
                val errorString = errorBody.string()
                body = Gson().fromJson(errorString, ErrorResponse::class.java)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        onError(body)
    }
}
