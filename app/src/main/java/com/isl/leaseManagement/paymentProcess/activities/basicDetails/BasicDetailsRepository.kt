package com.isl.leaseManagement.paymentProcess.activities.basicDetails

import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.dataClasses.requests.SubmitTaskRequest
import com.isl.leaseManagement.dataClasses.requests.UploadDocumentRequest
import com.isl.leaseManagement.dataClasses.responses.ApiSuccessFlagResponse
import com.isl.leaseManagement.dataClasses.responses.SingleMessageResponse
import com.isl.leaseManagement.dataClasses.responses.UploadDocumentResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class BasicDetailsRepository {
    private val api = ApiClient.request
    fun submitTask(
        callback: (ApiSuccessFlagResponse) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit,
        taskId: Int,
        body: SubmitTaskRequest
    ) {
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""
        val observable: Observable<ApiSuccessFlagResponse> =
            api!!.submitTask(userId = lsmUserId, taskId, body = body)
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ApiSuccessFlagResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: ApiSuccessFlagResponse) {
                    callback(t)
                }

                override fun onError(e: Throwable) {
                    errorCallBack(SingleMessageResponse(e.message))
                }

                override fun onComplete() {
                }
            })
    }

    fun uploadDocument(
        callback: (UploadDocumentResponse) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit,
        taskId: Int,
        body: UploadDocumentRequest
    ) {
        val observable: Observable<UploadDocumentResponse> =
            api!!.uploadDocument(taskId = taskId, body = body)
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<UploadDocumentResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: UploadDocumentResponse) {
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