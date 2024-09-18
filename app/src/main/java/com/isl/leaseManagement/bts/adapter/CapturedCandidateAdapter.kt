package com.isl.leaseManagement.bts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClasses.responses.ExistingCandidateListResponse
import com.isl.leaseManagement.utils.ClickInterfaces
import infozech.itower.R
import infozech.itower.databinding.CapturedCandidateListLayoutBinding


class CapturedCandidateAdapter(
    private val candidatesList: ArrayList<ExistingCandidateListResponse.ExistingCandidateListResponseItem>,
    private val ctx: BaseActivity,
    private val existingCandidateSelection: ClickInterfaces.ExistingCandidateSelection
) : RecyclerView.Adapter<CapturedCandidateAdapter.CapturedCandidateHolder>() {

    private lateinit var binding: CapturedCandidateListLayoutBinding

    override fun getItemCount(): Int = candidatesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CapturedCandidateHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.captured_candidate_list_layout,
                parent,
                false
            )
        return CapturedCandidateHolder(binding, existingCandidateSelection)
    }

    override fun onBindViewHolder(holder: CapturedCandidateHolder, position: Int) {
        holder.bindItems(candidatesList[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class CapturedCandidateHolder(
        private val binding: CapturedCandidateListLayoutBinding,
        private val existingCandidateSelection: ClickInterfaces.ExistingCandidateSelection
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(
            item: ExistingCandidateListResponse.ExistingCandidateListResponseItem?,
        ) {
            item ?: return
            item.landlordName?.let { binding.landlordNameTv.text = it }
            item.propertyId?.let { binding.propertyIdValue.text = it }
            item.propertyDistrict?.let { binding.propertyDistrictValue.text = it }
            item.landlordId?.let { binding.landLordIDValue.text = it }
            item.propertyCity?.let { binding.propertyCityValue.text = it }
        }
    }
}