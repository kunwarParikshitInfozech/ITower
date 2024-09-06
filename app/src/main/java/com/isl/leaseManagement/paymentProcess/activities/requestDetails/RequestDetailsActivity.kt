package com.isl.leaseManagement.paymentProcess.activities.requestDetails

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.isl.itower.MyApp
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.utils.LocalTempVarStore.requestRemarkFieldData
import infozech.itower.R
import infozech.itower.databinding.ActivityRequestDetailsBinding

class RequestDetailsActivity : BaseActivity() {

    private lateinit var viewModel: RequestDetailsViewModel

    private lateinit var binding: ActivityRequestDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_details)
        init()
    }

    private fun init() {
        val factory = RequestDetailsViewModelFactory(RequestDetailsRepository())
        viewModel = ViewModelProvider(this, factory)[RequestDetailsViewModel::class.java]
        setClickListeners()
        getRequestDetailsData()
    }

    private fun getRequestDetailsData() {
        MyApp.localTempVarStore?.let { tempVarStorage ->
            tempVarStorage.taskResponse?.let { taskResponse ->
                taskResponse.requestId?.let {
                    callRequestDetails(it)
                }
            }
        }
    }


    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }

    }


    private fun callRequestDetails(requestId: String) {
        showProgressBar()
        viewModel.getTaskRequestDetails(
            { successResponse ->
                successResponse?.let { requestDetails ->
                    binding.landlordNameEt.text = requestDetails.landlordName ?: ""
                    binding.landlordNameArabicEt.text = requestDetails.landlordNameInArabic ?: ""
                    binding.landlordIdEt.text = requestDetails.landlordId ?: ""
                    binding.landlordTypeEt.text = requestDetails.landlordType ?: ""
                    binding.landlordDistrictArabicEt.text = requestDetails.districtInArabic ?: ""
                    binding.landlordCityArabicEt.text = requestDetails.districtInArabic ?: ""
                    binding.landlordDistrictEt.text = requestDetails.district ?: ""
                    binding.landlordCityEt.text = requestDetails.city ?: ""
                    binding.landlordIdNumberEt.text = requestDetails.landlordNationalIdNumber ?: ""
                    binding.landlordTelephoneEt.text =
                        (requestDetails.landlordTelephone ?: "").toString()
                    binding.landlordEmailEt.text = requestDetails.landlordEmail ?: ""
                    binding.commercialRegNumberEt.text =
                        requestDetails.commercialRegistrationNumber ?: ""
                    binding.isVatApplicableEt.text = requestDetails.isVATApplicable ?: ""

                    binding.remarksValueTv.text = requestRemarkFieldData
                        ?: ""  //only for baladiya, getting from start task baladiya
                }
                hideProgressBar()
            },
            { errorMessage ->
                hideProgressBar()
            },
            requestId = requestId,
        )
    }


    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
}