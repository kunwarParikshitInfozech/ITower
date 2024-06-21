package com.isl.leaseManagement.fragments.tasks

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.isl.leaseManagement.activities.ShowLoaderActivity
import com.isl.leaseManagement.adapters.TasksAdapter
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.base.BaseFragment
import com.isl.leaseManagement.dataClass.responses.TaskResponse
import com.isl.leaseManagement.dataClass.responses.TasksSummaryResponse
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities.dpToPx
import infozech.itower.R
import infozech.itower.databinding.FragmentLsmTasksBinding
import infozech.itower.databinding.TaskDetailsPopupBinding

class LsmTasksFragment : BaseFragment() {

    private var tasksAdapter: TasksAdapter? = null
    private lateinit var binding: FragmentLsmTasksBinding
    private val repository = TaskRepository()
    private val viewModel: TaskViewModel by viewModels { TaskViewModelFactory(repository) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLsmTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setClickListeners()
        callTasksSummaryApi()
    }

    private fun setClickListeners() {
        binding.assignedTask.root.setOnClickListener {
            showTasksList()
        }
        val toolbar: View = requireActivity().findViewById(R.id.leaseManagementToolbar)
        val toolbarBackIv = toolbar.findViewById<ImageView>(R.id.toolbarBackIv)
        toolbarBackIv.setOnClickListener { reverseLayoutsVisibility() }
    }

    private fun showTasksList() {
        reverseLayoutsVisibility()
        callTasksApi()
    }

    private fun showTaskDetailsPopup(taskResponse: TaskResponse) {
        context ?: return
        val dialog = Dialog(requireActivity())
        val binding = TaskDetailsPopupBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        val heightInPixels = dpToPx(requireActivity(), 480)
        layoutParams.height = heightInPixels
        layoutParams.gravity = Gravity.BOTTOM
        dialog.window!!.attributes = layoutParams
        fillDataInTaskDetailsPopupAndAttachClicks(taskResponse, binding, dialog)
        dialog.show()
    }

    private fun fillDataInTaskDetailsPopupAndAttachClicks(
        taskResponse: TaskResponse,
        binding: TaskDetailsPopupBinding,
        dialog: Dialog
    ) {
        binding.taskNumber.text = taskResponse.requestId ?: ""
        binding.taskName.text = taskResponse.taskName ?: ""
        binding.forecastStartDateValue.text = taskResponse.forecastStartDate ?: ""
        binding.taskPriority.text =
            taskResponse.requestPriority ?: "".also { binding.taskPriority.visibility = View.GONE }
        binding.forecastEndDateValue.text = taskResponse.forecastEndDate ?: ""
        binding.taskStatusValue.text = taskResponse.taskStatus ?: ""
        binding.slaStatusValue.text = taskResponse.slaStatus ?: ""
        binding.customerSiteIdValue.text = taskResponse.customerSiteId ?: ""
        binding.tawalSiteIdValue.text = taskResponse.siteId ?: ""
        binding.planStartDateValue.text = taskResponse.forecastStartDate ?: ""
        binding.planEndDateValue.text = taskResponse.forecastEndDate ?: ""

        binding.requester.text = taskResponse.requester ?: ""
        binding.regionTypeValue.text = taskResponse.region ?: ""
        binding.districtValue.text = taskResponse.district ?: ""
        binding.cityValue.text = taskResponse.city ?: ""

        binding.closeTv.setOnClickListener {
            dialog.dismiss()
        }
        binding.startActivity.setOnClickListener {
            baseActivity.launchActivity(ShowLoaderActivity::class.java)
        }
    }

    private fun reverseLayoutsVisibility() {
        val toolbar: View = requireActivity().findViewById(R.id.leaseManagementToolbar)
        val profileLayoutToolbar = toolbar.findViewById<ConstraintLayout>(R.id.profileDutyCl)
        val searchSortLayoutToolbar = toolbar.findViewById<ConstraintLayout>(R.id.searchSortCl)
        val isProfileVisible = profileLayoutToolbar.visibility == View.VISIBLE

        profileLayoutToolbar.visibility = if (isProfileVisible) View.GONE else View.VISIBLE
        searchSortLayoutToolbar.visibility = if (isProfileVisible) View.VISIBLE else View.GONE
        binding.assignedUnassignedRl.visibility = if (isProfileVisible) View.GONE else View.VISIBLE
        binding.tasksRv.visibility = if (isProfileVisible) View.VISIBLE else View.GONE
    }

    private fun callTasksSummaryApi() {
        binding.unAssignedTask.taskStatus.text = getString(R.string.unassigned_task)
        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchTasksSummary()
        viewModel.tasksSummary.observe(viewLifecycleOwner) { tasksSummary ->
            tasksSummary?.let {
                binding.progressBar.visibility = View.GONE
                val taskSummary = calculateTaskSummary(tasksSummary)
                binding.assignedTask.scheduledValue.text = taskSummary.totalAssignedTotal.toString()
                binding.assignedTask.addedTodayValue.text =
                    taskSummary.totalAssignedToday.toString()

                binding.unAssignedTask.scheduledValue.text =
                    taskSummary.totalUnassignedNum.toString()
                binding.unAssignedTask.addedTodayValue.text =
                    taskSummary.totalUnassignedToday.toString()
            }
        }
    }

    data class TaskSummary(
        val totalAssignedTotal: Int,
        val totalAssignedToday: Int,
        val totalUnassignedNum: Int,
        val totalUnassignedToday: Int
    )

    private fun calculateTaskSummary(tasks: List<TasksSummaryResponse>): TaskSummary {
        val assignedTasks = tasks.filter { it.taskStatus == "Assigned" }
        val unassignedTasks = tasks.filter { it.taskStatus == "Unassigned" }

        val totalAssignedNum = assignedTasks.sumOf { it.total ?: 0 }
        val totalAssignedToday = assignedTasks.sumOf { it.today ?: 0 }
        val totalUnassignedNum = unassignedTasks.sumOf { it.total ?: 0 }
        val totalUnassignedToday = unassignedTasks.sumOf { it.today ?: 0 }

        return TaskSummary(
            totalAssignedNum,
            totalAssignedToday,
            totalUnassignedNum,
            totalUnassignedToday
        )
    }

    private fun callTasksApi() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchTasks()
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            tasks?.let {
                binding.progressBar.visibility = View.GONE
                setTasksListAdapter(tasks)
            }
        }
    }

    private fun setTasksListAdapter(taskResponses: List<TaskResponse>) {
        tasksAdapter = TasksAdapter(taskResponses, activity as BaseActivity,
            object : ClickInterfaces.MyTasks {
                override fun myTaskClicked(taskResponse: TaskResponse) {
                    showTaskDetailsPopup(taskResponse)
                }

            })
        binding.tasksRv.layoutManager = LinearLayoutManager(activity)
        binding.tasksRv.adapter = tasksAdapter
    }

}