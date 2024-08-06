package com.isl.leaseManagement.paymentProcess.fragments.tasks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import com.isl.itower.MyApp
import com.isl.leaseManagement.base.BaseFragment
import com.isl.leaseManagement.dataClasses.responses.TasksSummaryResponse
import com.isl.leaseManagement.paymentProcess.activities.home.LsmHomeActivity
import com.isl.leaseManagement.room.db.MyDatabase
import com.isl.leaseManagement.utils.Utilities.formatSingleDigitNumber
import infozech.itower.R
import infozech.itower.databinding.FragmentLsmTasksBinding

class LsmTasksFragment : BaseFragment() {

    private lateinit var binding: FragmentLsmTasksBinding
    private val repository = TaskRepository()
    private val viewModel: TaskViewModel by viewModels { TaskViewModelFactory(repository) }
    private lateinit var startActivityLauncher: ActivityResultLauncher<Intent>
    var db: MyDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLsmTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() {
        loadProfileToolbar()
        setClickListeners()
        db = MyApp.getMyDatabase()
        callTasksSummaryApi()
    }

    private fun loadProfileToolbar() {
        val toolbar: View = requireActivity().findViewById(R.id.leaseManagementToolbar)
        val profileLayoutToolbar = toolbar.findViewById<ConstraintLayout>(R.id.profileDutyCl)
        val searchSortLayoutToolbar = toolbar.findViewById<ConstraintLayout>(R.id.searchSortCl)
        profileLayoutToolbar.visibility = View.VISIBLE
        searchSortLayoutToolbar.visibility = View.GONE
    }

    private fun setClickListeners() {
        binding.assignedTask.root.setOnClickListener {
            openLsmTaskListFragment(true)
        }
        binding.unAssignedTask.root.setOnClickListener {
            openLsmTaskListFragment(false)
        }

    }

    private fun openLsmTaskListFragment(assignedBoolean: Boolean) {
        LsmHomeActivity.taskIsAssigned = assignedBoolean
        LsmHomeActivity.myFragmentManager?.let { it1 ->
            LsmHomeActivity.openFragment(
                LsmTaskListFragment(),
                it1
            )
        }
    }

    private fun backClicked(toolbar: View) {
        baseActivity.finish()
    }

    private fun callTasksSummaryApi() {
        binding.unAssignedTask.taskStatus.text = getString(R.string.unassigned_task)
        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchTasksSummary()
        viewModel.tasksSummary.observe(viewLifecycleOwner) { tasksSummary ->
            tasksSummary?.let {
                binding.progressBar.visibility = View.GONE
                val taskSummary = calculateTaskSummary(tasksSummary)
                binding.assignedTask.scheduledValue.text =
                    formatSingleDigitNumber(taskSummary.totalAssignedTotal.toString())
                binding.assignedTask.addedTodayValue.text =
                    formatSingleDigitNumber(taskSummary.totalAssignedToday.toString())

                binding.unAssignedTask.scheduledValue.text =
                    formatSingleDigitNumber(taskSummary.totalUnassignedNum.toString())
                binding.unAssignedTask.addedTodayValue.text =
                    formatSingleDigitNumber(taskSummary.totalUnassignedToday.toString())
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

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

}