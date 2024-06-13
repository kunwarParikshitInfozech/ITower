package com.isl.leaseManagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.isl.leaseManagement.base.BaseActivity
import infozech.itower.R
import infozech.itower.databinding.TasksListLayoutBinding


class TasksAdapter(
    private val parentTasksList: ArrayList<String>,
    private val ctx: BaseActivity,
) : RecyclerView.Adapter<TasksAdapter.TasksListHolder>() {

    private lateinit var binding: TasksListLayoutBinding

    override fun getItemCount(): Int = parentTasksList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksListHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.tasks_list_layout, parent, false)
        return TasksListHolder(binding, ctx)
    }

    override fun onBindViewHolder(holder: TasksListHolder, position: Int) {
        holder.bindItems(parentTasksList[position], position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class TasksListHolder(
        private val binding: TasksListLayoutBinding,
        private val ctx: BaseActivity,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(item: String?, chairSelectedPosition: Int) {
            item?.let {
            }
        }
    }
}