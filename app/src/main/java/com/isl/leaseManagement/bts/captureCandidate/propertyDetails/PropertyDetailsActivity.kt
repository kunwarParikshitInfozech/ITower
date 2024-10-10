package com.isl.leaseManagement.bts.captureCandidate.propertyDetails

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.isl.leaseManagement.base.BaseActivity
import infozech.itower.R
import infozech.itower.databinding.ActivityPropertyDetailsBinding

class PropertyDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityPropertyDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_details)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_property_details)

        init()
    }

    private fun init() {

    }

    private fun loadEssentialProperty() {

    }

}