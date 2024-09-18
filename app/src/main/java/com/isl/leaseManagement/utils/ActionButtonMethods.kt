package com.isl.leaseManagement.utils

import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.isl.itower.MyApp
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.bts.captureCandidate.captureNewCandidate.CaptureNewCandidateActivity
import com.isl.leaseManagement.bts.captureCandidate.existingCandidate.SelectExistingCandidateActivity
import com.isl.leaseManagement.bts.captureCandidate.requestDetailsBts.BtsRequestDetailsActivity
import com.isl.leaseManagement.common.activities.addAdditionalDoc.AddAdditionalDocumentActivity
import com.isl.leaseManagement.dataClasses.responses.TaskResponse
import infozech.itower.R
import infozech.itower.databinding.ActionsPopupCaptureCandidateBinding
import infozech.itower.databinding.TaskDetailsPopupBinding

object ActionButtonMethods {
    object CaptureCandidateActions {
        fun showActionPopup(baseActivity: BaseActivity) {
            val dialog = Dialog(baseActivity)
            val binding = ActionsPopupCaptureCandidateBinding.inflate(baseActivity.layoutInflater)
            dialog.setContentView(binding.root)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window!!.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            val heightInPixels = Utilities.dpToPx(baseActivity, 480)
            layoutParams.height = heightInPixels
            layoutParams.gravity = Gravity.BOTTOM
            dialog.window!!.attributes = layoutParams
            dialog.show()
            binding.closeTv.setOnClickListener {
                dialog.dismiss()
            }
            binding.requestDetailsTv.setOnClickListener {
                dialog.dismiss()
                baseActivity.launchActivity(BtsRequestDetailsActivity::class.java)
            }
            binding.taskDetailTv.setOnClickListener {
                dialog.dismiss()
                MyApp.localTempVarStore.taskResponse?.let { it1 ->
                    showTaskDetailsPopupWithoutStart(
                        baseActivity,
                        it1
                    )
                }
            }
            binding.addAdditionalDocTv.setOnClickListener {
                dialog.dismiss()
                baseActivity.launchActivity(AddAdditionalDocumentActivity::class.java)
            }
            binding.addNewCandidateTv.setOnClickListener {
                dialog.dismiss()
                baseActivity.launchActivity(CaptureNewCandidateActivity::class.java)
            }
            binding.selectExistingCandidateTv.setOnClickListener {
                dialog.dismiss()
                baseActivity.launchActivity(SelectExistingCandidateActivity::class.java)
            }
        }

        private fun showTaskDetailsPopupWithoutStart(
            context: BaseActivity,
            taskResponse: TaskResponse
        ) {
            context ?: return
            val dialog = Dialog(context)
            val binding = TaskDetailsPopupBinding.inflate(context.layoutInflater)
            dialog.setContentView(binding.root)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window!!.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            val heightInPixels = Utilities.dpToPx(context, 480)
            layoutParams.height = heightInPixels
            layoutParams.gravity = Gravity.BOTTOM
            dialog.window!!.attributes = layoutParams
            fillDataInTaskDetailsPopupAndAttachClicks(
                context = context,
                taskResponse,
                binding,
                dialog
            )
            dialog.show()
        }

        private fun fillDataInTaskDetailsPopupAndAttachClicks(
            context: BaseActivity,
            taskResponse: TaskResponse,
            binding: TaskDetailsPopupBinding,
            dialog: Dialog
        ) {
            binding.taskNumber.text = (taskResponse.siteId ?: "").toString()
            binding.taskName.text = taskResponse.taskName ?: ""
            binding.forecastStartDateValue.text =
                taskResponse.forecastStartDate?.let { Utilities.getDateFromISO8601(it) }
            binding.taskPriority.text =
                taskResponse.requestPriority ?: "".also {
                    binding.taskPriority.visibility = View.GONE
                }
            if (taskResponse.requestPriority != null && taskResponse.requestPriority == "Low") {
                binding.taskPriority.setTextColor(context.getColor(R.color.color_34C759))
                binding.taskPriority.background =
                    context.getDrawable(R.drawable.rounded_bg_tv_green)
            }
            if (taskResponse.slaStatus != null && taskResponse.slaStatus == "On Time") {
                binding.slaStatusValue.setTextColor(context.getColor(R.color.color_34C759))
            }
            binding.forecastEndDateValue.text =
                taskResponse.forecastEndDate?.let { Utilities.getDateFromISO8601(it) }
            //     binding.taskStatusValue.text = taskResponse.taskStatus ?: ""
            binding.taskStatusValue.text =
                "In-Progress" // as the task would always be in progress state and this change is not saved while saving in room , to getupdated status we have to call api again.
            binding.slaStatusValue.text = taskResponse.slaStatus ?: ""
            binding.customerSiteIdValue.text = taskResponse.customerSiteId ?: ""
            binding.tawalSiteIdValue.text = taskResponse.siteId ?: ""
            binding.planStartDateValue.text =
                taskResponse.startDate?.let { Utilities.getDateFromISO8601(it) }
            binding.planEndDateValue.text =
                taskResponse.endDate?.let { Utilities.getDateFromISO8601(it) }
            binding.requesterValue.text = taskResponse.requester ?: ""
            binding.regionTypeValue.text = taskResponse.region ?: ""
            binding.districtValue.text = taskResponse.district ?: ""
            binding.cityValue.text = taskResponse.city ?: ""
            binding.siteTowerTypeValue.text = taskResponse.towerType ?: ""
            binding.actualStartDateValue.text =
                taskResponse.actualStartDate?.let { Utilities.getDateFromISO8601(it) }

            taskResponse.taskStatus?.let {
                if (it != "Assigned") {
                    binding.llStartActivity.visibility = View.GONE
                }
            }
            binding.closeTv.setOnClickListener {
                dialog.dismiss()
            }
            binding.startActivity.visibility = View.GONE
            binding.unAssign.visibility = View.GONE
            taskResponse.taskId?.let { taskId ->
                binding.unAssign.setOnClickListener {
//                callUnAssignAPi(taskId,context)
                }
            }
        }
    }
}