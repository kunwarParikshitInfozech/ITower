package com.isl.leaseManagement.bts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClasses.responses.ExistingCandidateListResponse
import com.isl.leaseManagement.utils.ClickInterfaces
import infozech.itower.R
import infozech.itower.databinding.ExistingCandidateListLayoutBinding


class ExistingCandidateListAdapter(
    private val candidatesList: ArrayList<ExistingCandidateListResponse.ExistingCandidateListResponseItem>,
    private val ctx: BaseActivity,
    private val existingCandidateSelection: ClickInterfaces.ExistingCandidateSelection
) : RecyclerView.Adapter<ExistingCandidateListAdapter.CandidateListHolder>() {

    private lateinit var binding: ExistingCandidateListLayoutBinding

    override fun getItemCount(): Int = candidatesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateListHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.existing_candidate_list_layout,
                parent,
                false
            )
        return CandidateListHolder(binding, existingCandidateSelection, ctx)
    }

    override fun onBindViewHolder(holder: CandidateListHolder, position: Int) {
        holder.bindItems(candidatesList[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class CandidateListHolder(
        private val binding: ExistingCandidateListLayoutBinding,
        private val existingCandidateSelection: ClickInterfaces.ExistingCandidateSelection,
        val ctx: BaseActivity
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(
            item: ExistingCandidateListResponse.ExistingCandidateListResponseItem?,
        ) {
            item ?: return
            binding.landlordNameTv.text = item.landlordName ?: ctx.getString(R.string.na)   // NA not working for default in RV as placeholder is set in xml but android does not recreate at evry 3rd time
            binding.propertyIdValue.text = item.propertyId ?: ctx.getString(R.string.na)
            binding.propertyDistrictValue.text = item.propertyDistrict ?: ctx.getString(R.string.na)
            binding.landLordIDValue.text = item.landlordId ?: ctx.getString(R.string.na)
            binding.propertyCityValue.text = item.propertyCity ?: ctx.getString(R.string.na)


            binding.clCandidates.setOnClickListener {
                binding.selectedTickIv.visibility =
                    if (binding.selectedTickIv.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                existingCandidateSelection.candidateClicked(item)
            }
        }
    }

    fun addMoreCandidates(newItems: List<ExistingCandidateListResponse.ExistingCandidateListResponseItem>) {
        val startPosition = candidatesList.size
        candidatesList.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }

    fun removeAllData() {
        candidatesList.clear()
    }
}