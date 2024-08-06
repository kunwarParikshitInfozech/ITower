package com.isl.leaseManagement.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClasses.responses.TaskResponse
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities.getDateFromISO8601
import infozech.itower.R
import infozech.itower.databinding.SavedTasksListLayoutBinding


class SavedTasksAdapter(
    private val tasksList: List<TaskResponse>,
    private val ctx: BaseActivity,
    private val clickListener: ClickInterfaces.MyTasks
) : RecyclerView.Adapter<SavedTasksAdapter.TasksListHolder>() {

    private lateinit var binding: SavedTasksListLayoutBinding

    override fun getItemCount(): Int = tasksList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksListHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.saved_tasks_list_layout, parent, false)
        return TasksListHolder(binding, ctx, clickListener)
    }

    override fun onBindViewHolder(holder: TasksListHolder, position: Int) {
        holder.bindItems(tasksList[position], position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class TasksListHolder(
        private val binding: SavedTasksListLayoutBinding,
        private val ctx: BaseActivity,
        private val clickListener: ClickInterfaces.MyTasks
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(item: TaskResponse?, taskSelectedPosition: Int) {
            item ?: return
            item.siteId?.let { binding.taskNumber.text = it }
            item.taskName?.let { binding.taskName.text = it }
            item.taskStatus?.let { binding.assigned.text = it }
            var slaDuration = ""
            var slaUnit = ""
            item.slaDuration?.let { slaDuration = it.toString() }
            item.slaUnit?.let { slaUnit = it }
            binding.taskDays.text = "$slaDuration $slaUnit"
            item.slaStatus?.let { binding.slaStatus.text = it }
            item.requestPriority?.let {
                binding.taskPriority.text = it
                if (it == "Low") {
                    binding.taskPriority.setTextColor(ctx.getColor(R.color.color_34C759))
                    binding.taskPriority.background =
                        ctx.getDrawable(R.drawable.rounded_bg_tv_green)
                }
            }
            item.actualStartDate?.let { binding.taskDate.text = getDateFromISO8601(it) }

            binding.clTasks.setOnClickListener { clickListener.myTaskClicked(item) }
        }
    }
}