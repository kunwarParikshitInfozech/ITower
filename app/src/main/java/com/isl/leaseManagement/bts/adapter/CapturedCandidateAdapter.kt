package com.isl.leaseManagement.bts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClasses.responses.ExistingCandidateListResponse
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities
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
        return CapturedCandidateHolder(binding, existingCandidateSelection, ctx)
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
        private val existingCandidateSelection: ClickInterfaces.ExistingCandidateSelection,
        private val ctx: BaseActivity
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(
            item: ExistingCandidateListResponse.ExistingCandidateListResponseItem?,
        ) {
            item ?: return
            item.landlordName?.let {
                binding.landlordNameTv.text =
                    Utilities.ellipTextSizeToSpecificLength(it, 15)  //default 24
            }
            item.propertyId?.let { binding.propertyIdValue.text = it }
            item.propertyDistrict?.let { binding.propertyDistrictValue.text = it }
            item.landlordId?.let { binding.landLordIDValue.text = it }
            item.propertyCity?.let { binding.propertyCityValue.text = it }

            binding.selectedTickIv.visibility = if (item.isItemSelected) View.VISIBLE else View.GONE

            binding.llCandidateDetails.setOnClickListener {
                binding.selectedTickIv.visibility =
                    if (binding.selectedTickIv.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                existingCandidateSelection.candidateClicked(item)
            }

            if (
                item.isPropertyValid && item.isLandlordValid && item.isAccountValid &&
                item.isDelegateValid && item.isDocValid
            ) {
                binding.validOrNotImageView.setImageDrawable(ctx.getDrawable(R.drawable.green_tick))
                binding.fillDetailsText.visibility = View.GONE
                binding.clCandidates.background =
                    (ctx.getDrawable(R.drawable.white_rounded_rect_bg))
            }

            binding.landlordNameTv.setOnClickListener {
                existingCandidateSelection.candidateNameSelected(
                    item
                )
            }
        }
    }

    fun selectAllCandidates() {
        for (i in candidatesList.indices) {
            candidatesList[i].isItemSelected =
                true // this will only help in showing selected item blue tick, not in maintaining list
        }
        notifyDataSetChanged()
    }

    fun unselectAllCandidates() {
        for (i in candidatesList.indices) {
            candidatesList[i].isItemSelected =
                false
        }
        notifyDataSetChanged()
    }
}