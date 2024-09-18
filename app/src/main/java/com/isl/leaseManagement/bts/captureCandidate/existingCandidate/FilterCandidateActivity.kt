package com.isl.leaseManagement.bts.captureCandidate.existingCandidate

import android.app.Activity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.bts.adapter.DistrictListAdapter
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.MessageConstants.ErrorMessages.selectedDistrictIdIsEmpty
import com.isl.leaseManagement.utils.Utilities
import infozech.itower.R
import infozech.itower.databinding.ActivityFilterCandidateBinding

class FilterCandidateActivity : BaseActivity() {

    private lateinit var binding: ActivityFilterCandidateBinding
    var selectedPair: Pair<Int?, String?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter_candidate)
        init()
    }

    private fun init() {
        initializeDistrictListRv()
        clickListeners()
        getLastDistrictSelectedAndFill()
    }

    private fun clickListeners() {
        binding.applyBtn.setOnClickListener {
            if (selectedPair == null) {
                showToastMessage(selectedDistrictIdIsEmpty)
                return@setOnClickListener
            }
            val (id, name) = selectedPair!!
            if (id == null || name == null) {
                showToastMessage(selectedDistrictIdIsEmpty)
                return@setOnClickListener
            }
            intent.putExtra(
                AppConstants.ActivityResultKeys.FilterCandidateActivity.districtSelectedName,
                name
            )
            intent.putExtra(
                AppConstants.ActivityResultKeys.FilterCandidateActivity.districtSelectedID,
                id
            )
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        binding.backIv.setOnClickListener { finish() }
    }

    private fun initializeDistrictListRv() {
        val districtID =
            intent.getIntExtra(
                AppConstants.ActivityResultKeys.FilterCandidateActivity.districtSelectedID,
                0
            )

        val districtListPair = Utilities.getDistrictFromRegion(null)
        districtListPair ?: return
        val districtAdapter =
            DistrictListAdapter(districtListPair, this, object : ClickInterfaces.DistrictSelection {
                override fun districtSelected(pair: Pair<Int?, String?>) {
                    val (id, name) = pair
                    if (id == null || name == null) {
                        showToastMessage(selectedDistrictIdIsEmpty)
                        return
                    }
                    selectedPair = pair
                    binding.districtSelectedTv.text = name
                }
            }, districtIDAlreadySelected = districtID)
        binding.districtListingRv.layoutManager = LinearLayoutManager(this)
        binding.districtListingRv.adapter = districtAdapter

    }

    private fun getLastDistrictSelectedAndFill() {
        val districtName =
            intent.getStringExtra(AppConstants.ActivityResultKeys.FilterCandidateActivity.districtSelectedName)
        val districtId =
            intent.getIntExtra(
                AppConstants.ActivityResultKeys.FilterCandidateActivity.districtSelectedID,
                0
            )
        if (districtName != null) {
            binding.districtSelectedTv.text = districtName
        }
        selectedPair = Pair(districtId, districtName)
    }

}