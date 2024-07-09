package com.isl.leaseManagement.activities.loader

import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.dataClass.requests.StartTaskRequest
import com.isl.leaseManagement.dataClass.responses.SingleMessageResponse
import com.isl.leaseManagement.dataClass.responses.StartTaskResponse
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody


class StartDataRepository {
    private val api = ApiClient.request
    fun startTask(
        callback: (StartTaskResponse) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit,
        taskId: Int,
        body: StartTaskRequest
    ) {
        val observable: Observable<StartTaskResponse> = api!!.startTask(taskId, body = body)
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<StartTaskResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: StartTaskResponse) {
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