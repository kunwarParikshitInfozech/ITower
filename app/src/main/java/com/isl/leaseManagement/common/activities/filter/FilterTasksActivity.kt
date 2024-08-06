package com.isl.leaseManagement.common.activities.filter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.utils.AppConstants
import infozech.itower.R
import infozech.itower.databinding.ActivityFilterTasksBinding

class FilterTasksActivity : BaseActivity() {
    private lateinit var binding: ActivityFilterTasksBinding

    private var emptyString = ""
    private var tasksStatusSelected = emptyString
    private var slaSelected = emptyString
    private var prioritySelected = emptyString
    private var allTasks = "All Tasks"  //nothing need to be passed
    private var assignedTask = "Assigned"
    private var unAssignedTask = "Unassigned"
    private var delayed = "Delayed"
    private var onTime = "On Time"
    private var high = "High"
    private var medium = "Medium"
    private var low = "Low"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter_tasks)
        init()
    }

    private fun init() {
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.clearAllTv.setOnClickListener {
            recreate()
        }
        binding.applyBtn.setOnClickListener {
            val resultIntent = Intent() // Create an intent to hold data
            resultIntent.putExtra(
                AppConstants.ActivityResultKeys.FilterActivity.task,
                tasksStatusSelected
            )
            if (slaSelected==onTime){
                slaSelected = "On_Time"
            }
            resultIntent.putExtra(
                AppConstants.ActivityResultKeys.FilterActivity.slaStatus,
                slaSelected
            )
            resultIntent.putExtra(
                AppConstants.ActivityResultKeys.FilterActivity.priority,
                prioritySelected
            )
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        binding.cancelBtn.setOnClickListener { finish() }

        binding.taskTv.setOnClickListener {
            showHideFilters(binding.llTasksStatus)
            highlightSelectedFilter(it as TextView)
        }
        binding.slaStatus.setOnClickListener {
            showHideFilters(binding.llSlaStatus)
            highlightSelectedFilter(it as TextView)
        }
        binding.priority.setOnClickListener {
            showHideFilters(binding.llPriority)
            highlightSelectedFilter(it as TextView)
        }

        binding.allTaskTv.setOnClickListener {
            allTasksClicked(it)
        }
        binding.assignedTaskTv.setOnClickListener {
            assignedTaskClicked(it)
        }
        binding.unassignedTaskTv.setOnClickListener {
            unassignedTaskClicked(it)
        }
        binding.delayedTv.setOnClickListener {
            delayedClicked(it)
        }
        binding.onTimeTv.setOnClickListener {
            onTimeClicked(it)
        }
        binding.highTv.setOnClickListener {
            highClicked(it)
        }
        binding.mediumTv.setOnClickListener {
            mediumClicked(it)
        }
        binding.lowTv.setOnClickListener {
            lowClicked(it)
        }
    }

    private fun lowClicked(view: View) {
        prioritySelected = if (prioritySelected != low) low else emptyString
        alternateTvSelection(view as TextView)
        removeTvSelection(binding.highTv)
        removeTvSelection(binding.mediumTv)

        showSelectedFilter(prioritySelected, binding.taskPriorityTv)
    }

    private fun mediumClicked(view: View) {
        prioritySelected = if (prioritySelected != medium) medium else emptyString
        alternateTvSelection(view as TextView)
        removeTvSelection(binding.highTv)
        removeTvSelection(binding.lowTv)

        showSelectedFilter(prioritySelected, binding.taskPriorityTv)
    }

    private fun highClicked(view: View) {
        prioritySelected = if (prioritySelected != high) high else emptyString
        alternateTvSelection(view as TextView)
        removeTvSelection(binding.mediumTv)
        removeTvSelection(binding.lowTv)

        showSelectedFilter(prioritySelected, binding.taskPriorityTv)
    }

    private fun onTimeClicked(view: View) {
        slaSelected = if (slaSelected != onTime) onTime else emptyString
        alternateTvSelection(view as TextView)
        removeTvSelection(binding.delayedTv)

        showSelectedFilter(slaSelected, binding.slaSelectedTv)
    }

    private fun delayedClicked(view: View) {
        slaSelected = if (slaSelected != delayed) delayed else emptyString
        alternateTvSelection(view as TextView)
        removeTvSelection(binding.onTimeTv)

        showSelectedFilter(slaSelected, binding.slaSelectedTv)
    }

    private fun unassignedTaskClicked(view: View) {
        tasksStatusSelected =
            if (tasksStatusSelected != unAssignedTask) unAssignedTask else emptyString
        alternateTvSelection(view as TextView)
        removeTvSelection(binding.allTaskTv)
        removeTvSelection(binding.assignedTaskTv)

        showSelectedFilter(tasksStatusSelected, binding.taskSelectedTv)
    }

    private fun assignedTaskClicked(view: View) {
        tasksStatusSelected =
            if (tasksStatusSelected != assignedTask) assignedTask else emptyString
        alternateTvSelection(view as TextView)
        removeTvSelection(binding.allTaskTv)
        removeTvSelection(binding.unassignedTaskTv)

        showSelectedFilter(tasksStatusSelected, binding.taskSelectedTv)
    }

    private fun allTasksClicked(view: View) {
        tasksStatusSelected =
            if (tasksStatusSelected != allTasks) allTasks else emptyString

        alternateTvSelection(view as TextView)
        removeTvSelection(binding.assignedTaskTv)
        removeTvSelection(binding.unassignedTaskTv)

        showSelectedFilter(tasksStatusSelected, binding.taskSelectedTv)
    }

    private fun showSelectedFilter(textToShow: String, textView: TextView) {
        if (textToShow != emptyString) {
            textView.visibility = View.VISIBLE
            textView.text = textToShow
        } else {
            textView.visibility = View.GONE
        }
    }

    private fun highlightSelectedFilter(view: TextView) {
        binding.taskTv.background = getDrawable(R.drawable.faded_rect_bg_tv)
        binding.slaStatus.background = getDrawable(R.drawable.faded_rect_bg_tv)
        binding.priority.background = getDrawable(R.drawable.faded_rect_bg_tv)

        binding.taskTv.setTextColor(getColor(R.color.color_B1B7C8))
        binding.slaStatus.setTextColor(getColor(R.color.color_B1B7C8))
        binding.priority.setTextColor(getColor(R.color.color_B1B7C8))

        view.setTextColor(getColor(R.color.color_001E60))
        view.background = null
    }

    private fun showHideFilters(view: LinearLayout) {
        binding.llTasksStatus.visibility = View.GONE
        binding.llSlaStatus.visibility = View.GONE
        binding.llPriority.visibility = View.GONE
        view.visibility = View.VISIBLE
    }

    private fun alternateTvSelection(view: TextView) {
        val selectedColor = getColor(R.color.color_001E60)
        val selectedDrawable =
            getDrawable(R.drawable.small_faded_black_tick) // Assuming this is the unselected drawable
        val unselectedDrawable =
            getDrawable(R.drawable.small_black_tick) // Assuming this is the unselected drawable

        if (view.textColors.defaultColor == selectedColor) { // Selected case
            view.setTextColor(getColor(R.color.color_B1B7C8))
            view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                selectedDrawable, // Start drawable
                null,     // Top drawable (set to null for no top drawable)
                null,     // End drawable (set to null for no top drawable)
                null      // Bottom drawable (set to null for no bottom drawable)
            )
        } else { // Unselected case
            view.setTextColor(selectedColor)
            view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                unselectedDrawable, // Handle potential null drawable
                null,
                null,
                null
            )
        }
    }

    private fun removeTvSelection(view: TextView) {
        val selectedColor = getColor(R.color.color_B1B7C8)
        val unselectedDrawable =
            getDrawable(R.drawable.small_faded_black_tick) // Assuming this is the unselected drawable

        view.setTextColor(selectedColor)
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(
            unselectedDrawable, // Handle potential null drawable
            null,
            null,
            null
        )
    }


}