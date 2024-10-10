package com.isl.leaseManagement.bts.captureCandidate.taskInProgress

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.isl.itower.MyApp
import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.bts.captureCandidate.capturedCandidate.CapturedCandidateActivity
import com.isl.leaseManagement.common.activities.addtionalDocs.AdditionalDocumentsActivity
import com.isl.leaseManagement.dataClasses.responses.LocationsListResponse
import com.isl.leaseManagement.dataClasses.responses.TaskResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.ActionButtonMethods
import com.isl.leaseManagement.utils.MessageConstants
import com.isl.leaseManagement.utils.Utilities
import infozech.itower.R
import infozech.itower.databinding.ActivityCaptureCandidateTaskInProgressBinding
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CaptureCandidateTaskInProgressActivity : BaseActivity() {

    private lateinit var binding: ActivityCaptureCandidateTaskInProgressBinding
    private val api = ApiClient.request

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_capture_candidate_task_in_progress
        )
        init()
    }

    private fun init() {
        setClickListeners()
        MyApp.localTempVarStore.taskResponse?.let { fillTaskDetailsData(it) }
        getRegionDistrictForFutureUse()
    }

    private fun fillTaskDetailsData(taskResponse: TaskResponse) {
        taskResponse.taskStatus
        binding.taskId.text = (taskResponse.siteId ?: "").toString()
        binding.taskName.text = taskResponse.taskName ?: ""
        taskResponse.forecastEndDate?.let {
            binding.dueByValue.text = Utilities.getDateFromISO8601(it)
        }
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
//        binding.additionalDocuments.setOnClickListener {
//            launchActivity(AdditionalDocumentsActivity::class.java)
//        }
        binding.actionBtn.setOnClickListener {
            ActionButtonMethods.Actions.showActionPopup(
                this, ActionButtonMethods.ActionOpeningProcess.BtsCaptureCandidate
            )
        }
        binding.captureCandidateBtn.setOnClickListener { launchActivity(CapturedCandidateActivity::class.java) }
    }

    private fun getRegionDistrictForFutureUse() {
        val observable: Observable<LocationsListResponse> =
            api!!.getLocationsList(
                tenantId = KotlinPrefkeeper.leaseManagementUserID!!,
            )
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :
                Observer<LocationsListResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: LocationsListResponse) {
                    KotlinPrefkeeper.locationsList = t
                }

                override fun onError(e: Throwable) {
                    showToastMessage(MessageConstants.ErrorMessages.unableToGetLocationList)
                }

                override fun onComplete() {
                }
            })
    }

}