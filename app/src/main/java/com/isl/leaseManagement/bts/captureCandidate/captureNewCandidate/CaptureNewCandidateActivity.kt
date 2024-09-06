package com.isl.leaseManagement.bts.captureCandidate.captureNewCandidate

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.utils.ActionButtonMethods
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
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.actionBtn.setOnClickListener {
            ActionButtonMethods.CaptureCandidateActions.showActionPopup(
                this
            )
        }
    }

}