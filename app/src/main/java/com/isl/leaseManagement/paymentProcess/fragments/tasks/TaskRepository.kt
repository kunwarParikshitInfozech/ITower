package com.isl.leaseManagement.paymentProcess.fragments.tasks

import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.dataClasses.responses.ApiSuccessFlagResponse
import com.isl.leaseManagement.dataClasses.responses.SingleMessageResponse
import com.isl.leaseManagement.dataClasses.responses.TaskResponse
import com.isl.leaseManagement.dataClasses.responses.TasksSummaryResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody


class TaskRepository {
    private val api = ApiClient.request

    fun getTasks(
        userId: Int,
        callback: (List<TaskResponse>?) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit,
        requestStatus: String?,
        taskStatus: String?,
        slaStatus: String?,
        requestPriority: String?
    ) {
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""
        val observable: Observable<List<TaskResponse>> = api!!.getTasks(
            //         userId,
            lsmUserId,
            requestStatus = requestStatus,
            taskStatus = taskStatus,
            slaStatus = slaStatus,
            requestPriority = requestPriority
        )
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<TaskResponse>> {
                override fun onSubscribe(d: Disposable) {
                    // Disposable
                }

                override fun onNext(t: List<TaskResponse>) {
                    t.size
                    callback(t)
                    // Handle the response
                }

                override fun onError(e: Throwable) {
                    errorCallBack(SingleMessageResponse(e.message))
                    // Handle errors
                }

                override fun onComplete() {
                    // Handle completion
                }
            })
    }

    fun getTasksSummary(callback: (List<TasksSummaryResponse>?) -> Unit) {
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""
        val observable: Observable<List<TasksSummaryResponse>> = api!!.getTasksSummary(lsmUserId)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<TasksSummaryResponse>> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: List<TasksSummaryResponse>) {
                    callback(t)
                }

                override fun onError(e: Throwable) {
                    e.message
                }

                override fun onComplete() {
                }
            })
    }

    fun updateTaskStatus(
        callback: (ApiSuccessFlagResponse) -> Unit,
        errorCallBack: (SingleMessageResponse) -> Unit,
        taskId: Int,
        taskStatus: String,
        body: RequestBody
    ) {
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""
        val observable: Observable<ApiSuccessFlagResponse> =
            api!!.updateTaskStatus(userId = lsmUserId, taskId, taskStatus, body)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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