package com.isl.leaseManagement.common.activities.btsStartTask

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.isl.itower.MyApp
import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.bts.captureCandidate.taskInProgress.CaptureCandidateTaskInProgressActivity
import com.isl.leaseManagement.common.activities.home.LsmHomeActivity
import com.isl.leaseManagement.dataClasses.requests.StartTaskRequest
import com.isl.leaseManagement.dataClasses.responses.BtsCaptureCandidateStartResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import infozech.itower.R
import infozech.itower.databinding.ActivityBtsstartTaskBinding
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class BTSStartTaskActivity : BaseActivity() {    // start task for BTS processes

    private lateinit var binding: ActivityBtsstartTaskBinding
    private val api = ApiClient.request
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_btsstart_task)
        init()
    }

    private fun init() {
        getBtsStartTaskResponse()
    }

    private fun getBtsStartTaskResponse() {
        if (MyApp.localTempVarStore.isStartCalledFromRoom) {
            btsCalledFromRoom()
        } else {
            val startTaskRequest = StartTaskRequest(
                latitude = 0,
                longitude = 0,
                requestId = "",
                source = "",
                timestamp = ""
            )
            getBtsStartFromAPI(MyApp.localTempVarStore.taskId, startTaskRequest)

        }
    }

    private fun btsCalledFromRoom() {
        finish()
        launchActivity(CaptureCandidateTaskInProgressActivity::class.java)
    }

    private fun getBtsStartFromAPI(
        taskId: Int,
        body: StartTaskRequest
    ) {
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""
        val observable: Observable<BtsCaptureCandidateStartResponse> =
            api!!.startTaskCaptureCandidate(
                leasemanagementId = KotlinPrefkeeper.leaseManagementUserID!!,
                userId = lsmUserId, taskId, body = body
            )
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BtsCaptureCandidateStartResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: BtsCaptureCandidateStartResponse) {
                    saveStartResponseAndProceed(t)
                }

                override fun onError(e: Throwable) {
                    showToastMessage("Unable to start task!")
                }

                override fun onComplete() {
                }
            })
    }

    private fun saveStartResponseAndProceed(captureCandidateStartResponse: BtsCaptureCandidateStartResponse) {
        disposable =
            commonDatabase.captureCandidateStartDao()
                .insert(response = captureCandidateStartResponse)
                .subscribeOn(Schedulers.io())                 //saving irrespective of if data fetched from room or api as even with room, only same data will override so not an issue
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    finish()
                    launchActivity(CaptureCandidateTaskInProgressActivity::class.java)
                }, {// Handle error
                })
    }


    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        launchNewActivityCloseAllOther(LsmHomeActivity::class.java)
    }

}