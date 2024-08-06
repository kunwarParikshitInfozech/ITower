package com.isl.leaseManagement.paymentProcess.activities.taskInProgress

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.isl.itower.MyApp
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.common.activities.addAdditionalDoc.AddAdditionalDocumentActivity
import com.isl.leaseManagement.dataClasses.responses.TaskResponse
import com.isl.leaseManagement.common.activities.addtionalDocs.AdditionalDocumentsActivity
import com.isl.leaseManagement.paymentProcess.activities.basicDetails.BasicDetailsActivity
import com.isl.leaseManagement.paymentProcess.activities.requestDetails.RequestDetailsActivity
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.Utilities
import infozech.itower.R
import infozech.itower.databinding.ActionsPopupBinding
import infozech.itower.databinding.ActivityTaskInProgressBinding

class TaskInProgressActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskInProgressBinding
    private var paymentMethod: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_task_in_progress)
        init()
    }

    private fun init() {
        checkPaymentType()
        setCLickListeners()
        val taskResponse: TaskResponse? =
            intent.getSerializableExtra(AppConstants.IntentKeys.taskDetailIntentExtra) as TaskResponse?
        taskResponse?.let { fillTaskDetailsData(it) }
    }

    private fun checkPaymentType() {
        MyApp.localTempVarStore?.let { tempVarStorage ->
            tempVarStorage.startTaskResponse?.let { response ->
                response.data?.let { data ->
                    paymentMethod = data.paymentMethod
                }
            }
        }
    }

    private fun fillTaskDetailsData(taskResponse: TaskResponse) {
        taskResponse.taskStatus
        binding.taskId.text = (taskResponse.siteId ?: "").toString()
        binding.taskName.text = taskResponse.taskName ?: ""
        taskResponse.forecastEndDate?.let {
            binding.dueByValue.text = Utilities.getDateFromISO8601(it)
        }
    }

    private fun setCLickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.basicDetailsTv.setOnClickListener { launchActivity(BasicDetailsActivity::class.java) }
        binding.additionalDocuments.setOnClickListener {
            if (paymentMethod != AppConstants.KeyWords.paymentTypeCheck) {
                launchActivity(AdditionalDocumentsActivity::class.java)
            } else {
                showToastMessage("Payment method is check!")
            }
        }
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
            dialog.dismiss()
            launchActivity(RequestDetailsActivity::class.java)
        }
        binding.addAdditionalDocTv.setOnClickListener {
            dialog.dismiss()
            if (paymentMethod != AppConstants.KeyWords.paymentTypeCheck) {
                launchActivity(AddAdditionalDocumentActivity::class.java)
            } else {
                showToastMessage("Payment method is check!")
            }
        }
    }
}