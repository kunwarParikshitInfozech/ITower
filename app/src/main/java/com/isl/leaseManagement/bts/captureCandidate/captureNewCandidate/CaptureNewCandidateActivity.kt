package com.isl.leaseManagement.bts.captureCandidate.captureNewCandidate

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.utils.ActionButtonMethods
import com.isl.leaseManagement.utils.AppConstants
import infozech.itower.R
import infozech.itower.databinding.ActivityCaptureNewCandidateBinding

class CaptureNewCandidateActivity : BaseActivity() {

    private lateinit var binding: ActivityCaptureNewCandidateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_capture_new_candidate)
        init()
    }

    private fun init() {
        setClickListeners()
        checkIfNewCandidateOrExisting()
    }

    private fun checkIfNewCandidateOrExisting() {
        val candidateId = intent.getIntExtra(AppConstants.IntentKeys.candidateID, 0)
        val candidateRemarks =
            intent.getStringExtra(AppConstants.IntentKeys.candidateRejectionRemarks)

        binding.rejectionRemarksValue.text = candidateRemarks ?: ""
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.actionBtn.setOnClickListener {
            ActionButtonMethods.Actions.showActionPopup(
                this, ActionButtonMethods.ActionOpeningProcess.BtsCaptureCandidate
            )
        }
    }

}