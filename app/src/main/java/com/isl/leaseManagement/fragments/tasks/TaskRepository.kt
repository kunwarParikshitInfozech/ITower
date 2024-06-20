package com.isl.leaseManagement.fragments.tasks

import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.dataClass.responses.TaskResponse
import com.isl.leaseManagement.dataClass.responses.TasksSummaryResponse
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class TaskRepository {
    private val api = ApiClient.request

    fun getTasks(callback: (List<TaskResponse>?) -> Unit) {
        val observable: Observable<List<TaskResponse>> = api!!.getTasks()
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
                    e.message
                    // Handle errors
                }

                override fun onComplete() {
                    // Handle completion
                }
            })
    }

    fun getTasksSummary(callback: (List<TasksSummaryResponse>?) -> Unit) {
        val observable: Observable<List<TasksSummaryResponse>> = api!!.getTasksSummary()
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

}