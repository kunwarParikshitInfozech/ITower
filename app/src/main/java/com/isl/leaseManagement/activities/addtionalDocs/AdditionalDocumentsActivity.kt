package com.isl.leaseManagement.activities.addtionalDocs

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.isl.leaseManagement.activities.addAdditionalDoc.AddAdditionalDocumentActivity
import com.isl.leaseManagement.activities.requestDetails.RequestDetailsActivity
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClass.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.CustomTextView
import com.isl.leaseManagement.utils.Utilities
import com.isl.leaseManagement.utils.Utilities.getLastChars
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
//        KotlinPrefkeeper.additionalDocDataArray?.arrayOfSaveAdditionalDocument?.let {
//            addDocumentItems(
//                it
//            )
//        }
    }


    private fun addDocumentItems(documents: List<SaveAdditionalDocument>) {
        for (document in documents) {
            val inflater = LayoutInflater.from(this)
            val documentItemLayout = inflater.inflate(
                R.layout.additional_document_item,
                binding.documentContainer,
                false
            ) as LinearLayout

            // Set data for views based on your document object
            val docImage = documentItemLayout.findViewById<ImageView>(R.id.docImage)
            // You might need to set a default image here if the document doesn't have a specific type

            val docDetailsTxt = documentItemLayout.findViewById<CustomTextView>(R.id.docDetailsTxt)
            document.docName?.let {
                docDetailsTxt.text = getLastChars(it, 24)
            }

            val docSizeTxt = documentItemLayout.findViewById<CustomTextView>(R.id.docSizeTxt)
            document.docSize?.let {
                docSizeTxt.text = getLastChars(it, 18)
            }

            // Similar logic for downloadIv and iIconIv if needed

            binding.documentContainer.addView(documentItemLayout)
        }
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