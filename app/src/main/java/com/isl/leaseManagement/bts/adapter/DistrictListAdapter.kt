package com.isl.leaseManagement.bts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities.setDrawableStartToTextView
import infozech.itower.R
import infozech.itower.databinding.DistrictListLayoutBinding

class DistrictListAdapter(
    private val districtList: List<Pair<Int?, String?>>,
    private val ctx: BaseActivity,
    val districtSelection: ClickInterfaces.DistrictSelection,
    var districtIDAlreadySelected: Int?   // null if nothing was selected
) : RecyclerView.Adapter<DistrictListAdapter.DistrictListHolder>() {

    private lateinit var binding: DistrictListLayoutBinding
    private var selectedPosition: Int = -1 // To track the selected position

    override fun getItemCount(): Int = districtList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictListHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.district_list_layout, parent, false)
        return DistrictListHolder(binding, ctx)
    }

    override fun onBindViewHolder(holder: DistrictListHolder, position: Int) {
        holder.bindItems(districtList[position], position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class DistrictListHolder(
        private val binding: DistrictListLayoutBinding,
        private val ctx: BaseActivity
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindItems(item: Pair<Int?, String?>, position: Int) {
            val (districtId, districtName) = item
            districtName?.let { binding.districtTv.text = it }
            if (districtIDAlreadySelected != null && districtIDAlreadySelected != 0 && districtIDAlreadySelected == districtId) {
                districtIDAlreadySelected = 0
                selectedPosition = position
                binding.districtTv.setTextColor(ctx.getColor(R.color.color_001E60))
                ctx.getDrawable(R.drawable.small_black_tick)?.let {
                    setDrawableStartToTextView(
                        binding.districtTv,
                        it
                    )
                }
            }
            // Change the color based on selection
            if (position == selectedPosition) {
                binding.districtTv.setTextColor(ctx.getColor(R.color.color_001E60))
                ctx.getDrawable(R.drawable.small_black_tick)?.let {
                    setDrawableStartToTextView(
                        binding.districtTv,
                        it
                    )
                }
            } else {
                binding.districtTv.setTextColor(ctx.getColor(R.color.color_B1B7C8))
                ctx.getDrawable(R.drawable.small_faded_black_tick)?.let {
                    setDrawableStartToTextView(
                        binding.districtTv,
                        it
                    )
                }
            }

            // Handle click event
            binding.root.setOnClickListener {
                selectedPosition = position // Update the selected position
                notifyDataSetChanged()
                districtSelection.districtSelected(item)
            }
        }
    }
}
