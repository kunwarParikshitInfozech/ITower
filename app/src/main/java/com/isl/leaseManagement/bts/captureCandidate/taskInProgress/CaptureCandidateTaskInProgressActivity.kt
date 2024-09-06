package com.isl.leaseManagement.bts.captureCandidate.taskInProgress

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.isl.itower.MyApp
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.common.activities.addtionalDocs.AdditionalDocumentsActivity
import com.isl.leaseManagement.dataClasses.responses.TaskResponse
import com.isl.leaseManagement.utils.ActionButtonMethods
import com.isl.leaseManagement.utils.Utilities
import infozech.itower.R
import infozech.itower.databinding.ActivityCaptureCandidateTaskInProgressBinding

class CaptureCandidateTaskInProgressActivity : BaseActivity() {

    private lateinit var binding: ActivityCaptureCandidateTaskInProgressBinding

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
        binding.additionalDocuments.setOnClickListener {
            launchActivity(AdditionalDocumentsActivity::class.java)
        }
        binding.actionBtn.setOnClickListener {
            ActionButtonMethods.CaptureCandidateActions.showActionPopup(
                this
            )
        }
    }


}