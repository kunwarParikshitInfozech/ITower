package com.isl.leaseManagement.common.activities.startTask

import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.dataClasses.requests.StartTaskRequest
import com.isl.leaseManagement.dataClasses.responses.FieldWorkStartTaskResponse
import com.isl.leaseManagement.dataClasses.responses.PaymentStartTaskResponse
import com.isl.leaseManagement.dataClasses.responses.SingleMessageResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class StartTaskRepository {
    private val api = ApiClient.request

    fun startTaskPayment(
        callback: (PaymentStartTaskResponse) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit,
        taskId: Int,
        body: StartTaskRequest
    ) {
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""
        val observable: Observable<PaymentStartTaskResponse> =
            api!!.startTaskPayment(userId = lsmUserId, taskId, body = body)
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<PaymentStartTaskResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: PaymentStartTaskResponse) {
                    callback(t)
                }

                override fun onError(e: Throwable) {
                    errorCallBack(SingleMessageResponse(e.message))
                }

                override fun onComplete() {
                }
            })
    }

    fun startTaskFieldWork(
        callback: (FieldWorkStartTaskResponse) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit,
        taskId: Int,
        body: StartTaskRequest
    ) {
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""
        val observable: Observable<FieldWorkStartTaskResponse> =
            api!!.startTaskFieldWork(userId = lsmUserId, taskId, body = body)
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<FieldWorkStartTaskResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: FieldWorkStartTaskResponse) {
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