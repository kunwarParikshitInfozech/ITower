package com.isl.leaseManagement.activities.taskInProgress

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.isl.leaseManagement.activities.addAdditionalDoc.AddAdditionalDocumentActivity
import com.isl.leaseManagement.activities.addtionalDocs.AdditionalDocumentsActivity
import com.isl.leaseManagement.activities.basicDetails.BasicDetailsActivity
import com.isl.leaseManagement.activities.requestDetails.RequestDetailsActivity
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClass.responses.TaskResponse
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.Utilities
import infozech.itower.R
import infozech.itower.databinding.ActionsPopupBinding
import infozech.itower.databinding.ActivityTaskInProgressBinding

class TaskInProgressActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskInProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_task_in_progress)
        init()
    }

    private fun init() {
        setCLickListeners()
        val taskResponse: TaskResponse? =
            intent.getSerializableExtra(AppConstants.IntentKeys.taskDetailIntentExtra) as TaskResponse?
        taskResponse?.let { fillTaskDetailsData(it) }
    }

    private fun fillTaskDetailsData(taskResponse: TaskResponse) {
        taskResponse.taskStatus
        binding.taskRequestId.text = taskResponse.requestId ?: ""
        binding.taskName.text = taskResponse.taskName ?: ""
        binding.dueByValue.text = taskResponse.forecastEndDate ?: ""
    }

    private fun setCLickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.basicDetailsTv.setOnClickListener { launchActivity(BasicDetailsActivity::class.java) }
        binding.additionalDocuments.setOnClickListener { launchActivity(AdditionalDocumentsActivity::class.java) }
        binding.actionBtn.setOnClickListener { showTaskDetailsPopup() }
    }

    private fun showTaskDetailsPopup() {
        val dialog = Dialog(this)
        val binding = ActionsPopupBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        val heightInPixels = Utilities.dpToPx(this, 480)
        layoutParams.height = heightInPixels
        layoutParams.gravity = Gravity.BOTTOM
        dialog.window!!.attributes = layoutParams
        dialog.show()
        binding.closeTv.setOnClickListener {
            dialog.dismiss()
        }
        binding.requestDetailsTv.setOnClickListener {
            launchActivity(RequestDetailsActivity::class.java)
        }
        binding.addAdditionalDocTv.setOnClickListener {
            launchActivity(AddAdditionalDocumentActivity::class.java)
        }
    }
}