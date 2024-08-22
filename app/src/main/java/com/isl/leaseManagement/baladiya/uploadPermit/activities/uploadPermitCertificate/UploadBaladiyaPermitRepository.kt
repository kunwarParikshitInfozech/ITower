package com.isl.leaseManagement.baladiya.uploadPermit.activities.uploadPermitCertificate

import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.dataClasses.responses.ApiSuccessFlagResponse
import com.isl.leaseManagement.dataClasses.responses.FieldWorkStartTaskResponse
import com.isl.leaseManagement.dataClasses.responses.SingleMessageResponse
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class UploadBaladiyaPermitRepository {
    private val api = ApiClient.request

    fun updateBaladiyaApi(
        userId: String,
        taskId: Int,
        fieldWorkStartTaskResponse: FieldWorkStartTaskResponse,
        callback: (ApiSuccessFlagResponse) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit
    ) {
        val observable: Observable<ApiSuccessFlagResponse> =
            api!!.updateBaladiyaResponse(
                userId = userId,
                taskId = taskId,
                fieldWorkStartTaskResponse
            )
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

}