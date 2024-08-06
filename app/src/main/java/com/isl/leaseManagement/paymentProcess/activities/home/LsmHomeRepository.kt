package com.isl.leaseManagement.paymentProcess.activities.home

import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.dataClasses.requests.FetchDeviceIDRequest
import com.isl.leaseManagement.dataClasses.responses.FetchUserIdResponse
import com.isl.leaseManagement.dataClasses.responses.SingleMessageResponse
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class LsmHomeRepository {
    private val api = ApiClient.request
    fun fetchDeviceID(
        callback: (FetchUserIdResponse) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit,
        body: FetchDeviceIDRequest
    ) {
        val observable: Observable<FetchUserIdResponse> = api!!.fetchDeviceID( body = body)
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<FetchUserIdResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: FetchUserIdResponse) {
                    callback(t)
                }

                override fun onError(e: Throwable) {
                    errorCallBack(SingleMessageResponse(e.message))
                }

                override fun onComplete() {
                }
            })
    }

}