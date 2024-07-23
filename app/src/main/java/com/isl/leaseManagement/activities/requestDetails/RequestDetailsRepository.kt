package com.isl.leaseManagement.activities.requestDetails

import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.dataClass.responses.RequestDetailsResponse
import com.isl.leaseManagement.dataClass.responses.SingleMessageResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class RequestDetailsRepository {
    private val api = ApiClient.request
    fun getRequestDetails(
        callback: (RequestDetailsResponse) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit,
        requestId: String
    ) {
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""
        val observable: Observable<RequestDetailsResponse> =
            api!!.getTaskRequestDetails(userId = lsmUserId, requestId = requestId)
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<RequestDetailsResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: RequestDetailsResponse) {
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