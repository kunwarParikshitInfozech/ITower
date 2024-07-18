package com.isl.leaseManagement.activities.home

import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.dataClass.requests.FetchDeviceIDRequest
import com.isl.leaseManagement.dataClass.requests.UploadDocumentRequest
import com.isl.leaseManagement.dataClass.responses.SingleMessageResponse
import com.isl.leaseManagement.dataClass.responses.StartTaskResponse
import com.isl.leaseManagement.dataClass.responses.UploadDocumentResponse
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody


class LsmHomeRepository {
    private val api = ApiClient.request
    fun fetchDeviceID(
        callback: (FetchDeviceIDRequest) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit,
        body: FetchDeviceIDRequest
    ) {
        val observable: Observable<FetchDeviceIDRequest> = api!!.fetchDeviceID( body = body)
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<FetchDeviceIDRequest> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: FetchDeviceIDRequest) {
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