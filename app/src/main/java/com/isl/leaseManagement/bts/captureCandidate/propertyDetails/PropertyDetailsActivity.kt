package com.isl.leaseManagement.bts.captureCandidate.propertyDetails

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.isl.leaseManagement.base.BaseActivity
import infozech.itower.R
import infozech.itower.databinding.ActivityPropertyDetailsBinding

class PropertyDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityPropertyDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_property_details)
        init()
    }

    private fun init() {
        addTabsAndAttachClicks()
    }

    private fun addTabsAndAttachClicks() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Essential property"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Comprehensive property"))

        loadFragmentInFrameLayout(
            EssentialPropertyFragment(),
            binding.fragmentContainer.id
        )

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        loadFragmentInFrameLayout(
                            EssentialPropertyFragment(),
                            binding.fragmentContainer.id
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