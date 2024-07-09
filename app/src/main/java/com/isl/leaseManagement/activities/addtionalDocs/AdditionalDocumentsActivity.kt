package com.isl.leaseManagement.activities.addtionalDocs

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.isl.leaseManagement.activities.addAdditionalDoc.AddAdditionalDocumentActivity
import com.isl.leaseManagement.activities.requestDetails.RequestDetailsActivity
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.utils.Utilities
import infozech.itower.R
import infozech.itower.databinding.ActionsPopupBinding
import infozech.itower.databinding.ActivityAdditonalDocumentsBinding

class AdditionalDocumentsActivity : BaseActivity() {
    private lateinit var binding: ActivityAdditonalDocumentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_additonal_documents)
        init()
    }

    private fun init() {
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.actionsBtn.setOnClickListener { showTaskDetailsPopup() }
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