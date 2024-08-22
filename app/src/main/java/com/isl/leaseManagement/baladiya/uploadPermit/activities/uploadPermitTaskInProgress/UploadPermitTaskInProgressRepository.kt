package com.isl.leaseManagement.baladiya.uploadPermit.activities.uploadPermitTaskInProgress

import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.dataClasses.responses.ApiSuccessFlagResponse
import com.isl.leaseManagement.dataClasses.responses.BaladiyaNamesListResponse
import com.isl.leaseManagement.dataClasses.responses.SingleMessageResponse
import com.isl.leaseManagement.dataClasses.responses.SubmitBaladiyaFWRequest
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class UploadPermitTaskInProgressRepository {
    private val api = ApiClient.request

    fun getBaladiyaName(
        callback: (BaladiyaNamesListResponse) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit,
    ) {
        val observable: Observable<BaladiyaNamesListResponse> =
            api!!.getBaladiyaNameList()
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BaladiyaNamesListResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: BaladiyaNamesListResponse) {
                    callback(t)
                }

                override fun onError(e: Throwable) {
                    errorCallBack(SingleMessageResponse(e.message))
                }

                override fun onComplete() {
                }
            })
    }

    fun submitBaladiyaFieldWork(
        userId: String,
        taskId: Int,
        submitBaladiyaFWRequest: SubmitBaladiyaFWRequest,
        callback: (ApiSuccessFlagResponse) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit
    ) {
        val observable: Observable<ApiSuccessFlagResponse> =
            api!!.submitBaladiyaFieldWork(
                userId = userId,
                taskId = taskId,
                submitBaladiyaFWRequest
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