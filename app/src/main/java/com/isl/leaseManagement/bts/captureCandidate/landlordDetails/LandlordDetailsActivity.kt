package com.isl.leaseManagement.bts.captureCandidate.landlordDetails

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.bts.captureCandidate.propertyDetails.EssentialPropertyFragment
import com.isl.leaseManagement.utils.AppConstants
import infozech.itower.R
import infozech.itower.databinding.ActivityLandlordDetailsBinding

class LandlordDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityLandlordDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_landlord_details)
        init()
    }

    private fun init() {
        checkIfDataAlreadySaved()
        addTabsAndAttachClicks()
    }

    private var autoGeneratedID = 0
    private fun checkIfDataAlreadySaved() {
        autoGeneratedID = intent.getIntExtra(
            AppConstants.IntentKeys.autoGeneratedId,
            0
        )   // so, 0 means new candidate
    }

    private fun addTabsAndAttachClicks() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Essential Landlord"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Comprehensive Landlord"))

        val bundle = Bundle()
        bundle.putString(AppConstants.IntentKeys.autoGeneratedId, autoGeneratedID.toString())
        loadFragmentInFrameLayout(   //initial
            EssentialLandlordFragment(),
            binding.fragmentContainer.id,
            bundle
        )

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        loadFragmentInFrameLayout(
                            EssentialPropertyFragment(),
                            binding.fragmentContainer.id,
                            bundle
                        )
                    }

                    1 -> {
                        showToastMessage("Comprehensive Clicked!")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

    }

}