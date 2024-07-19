package com.isl.leaseManagement.fragments.tasks

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.isl.itower.MyApp
import com.isl.leaseManagement.activities.filter.FilterTasksActivity
import com.isl.leaseManagement.activities.startTask.StartTaskActivity
import com.isl.leaseManagement.adapters.SavedTasksAdapter
import com.isl.leaseManagement.adapters.TasksAdapter
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.base.BaseFragment
import com.isl.leaseManagement.dataClass.responses.TaskResponse
import com.isl.leaseManagement.dataClass.responses.TasksSummaryResponse
import com.isl.leaseManagement.room.db.MyDatabase
import com.isl.leaseManagement.room.entity.TaskResponsePOJO
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities
import com.isl.leaseManagement.utils.Utilities.dpToPx
import com.isl.leaseManagement.utils.Utilities.formatSingleDigitNumber
import infozech.itower.R
import infozech.itower.databinding.FragmentLsmTasksBinding
import infozech.itower.databinding.TaskDetailsPopupBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody

class LsmTasksFragment : BaseFragment() {

    private var tasksAdapter: TasksAdapter? = null
    private var savedTasksAdapter: SavedTasksAdapter? = null
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onResume() {
        super.onResume()
        callTasksSummaryApi()
    }

    private fun init() {
        setClickListeners()
        db = MyApp.getMyDatabase()
        registerActivityLauncher()
        getSavedTaskList()
    }

    private fun getSavedTaskList() {
        lifecycleScope.launch(Dispatchers.IO) {
            db?.taskResponseDao()?.allTaskResponse?.let { savedTaskList ->
                val taskResponseList = mutableListOf<TaskResponse>()
                for (taskResponsePojo in savedTaskList) {
                    val taskResponse = createTaskResponseDataClass(taskResponsePojo)
                    taskResponseList.add(taskResponse)
                    setSavedTasksListAdapter(taskResponseList.toList())
                }
            }
        }
    }

    private fun registerActivityLauncher() {
        startActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    var task =
                        result.data?.getStringExtra(AppConstants.ActivityResultKeys.FilterActivity.task)
                    var slaStatus =
                        result.data?.getStringExtra(AppConstants.ActivityResultKeys.FilterActivity.slaStatus)
                    var priority =
                        result.data?.getStringExtra(AppConstants.ActivityResultKeys.FilterActivity.priority)
                    if (task == "All Tasks") task = null
                    if (task == "") task = null
                    if (slaStatus == "") slaStatus = null
                    if (priority == "") priority = null
                    callTasksListApi(
                        taskStatus = task,
                        slaStatus = slaStatus,
                        requestPriority = priority
                    )
                } else {   // cancelled case
                }
            }
    }

    private fun setClickListeners() {
        binding.assignedTask.root.setOnClickListener {
            callTasksListApi(taskStatus = "Assigned")
        }
        binding.unAssignedTask.root.setOnClickListener {
            callTasksListApi(taskStatus = "Unassigned")
        }
        val toolbar: View = requireActivity().findViewById(R.id.leaseManagementToolbar)
        val toolbarBackIv = toolbar.findViewById<ImageView>(R.id.toolbarBackIv)
        toolbarBackIv.setOnClickListener {
            backClicked(toolbar)
        }
        val toolbarFilter = toolbar.findViewById<ImageView>(R.id.toolbarFilterIv)
        toolbarFilter.setOnClickListener {
            val intent =
                Intent(context, FilterTasksActivity::class.java) // Replace with your activity class
            startActivityLauncher.launch(intent)
        }

    }

    private fun backClicked(toolbar: View) {
        val profileLayoutToolbar = toolbar.findViewById<ConstraintLayout>(R.id.profileDutyCl)
        val isProfileVisible = profileLayoutToolbar.visibility == View.VISIBLE
        if (!isProfileVisible) {
            reverseLayoutsVisibility()
        } else {
            baseActivity.finish()
        }
    }


    private fun callTasksListApi(
        requestStatus: String? = null,
        taskStatus: String? = null,
        slaStatus: String? = null,
        requestPriority: String? = null
    ) {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchTasks(
            //       baseActivity.userId,
            123,
            { taskList ->
                if (taskList != null) {
                    binding.progressBar.visibility = View.GONE
                    setTasksListAdapter(taskList)

                    val toolbar: View = requireActivity().findViewById(R.id.leaseManagementToolbar)
                    val profileLayoutToolbar =
                        toolbar.findViewById<ConstraintLayout>(R.id.profileDutyCl)
                    val isProfileVisible = profileLayoutToolbar.visibility == View.VISIBLE
                    //    if (isProfileVisible) {
                    reverseLayoutsVisibility()
                    //}
                } else {
                    baseActivity.showToastMessage("No task found!")
                }
            },
            { errorMessage ->
                baseActivity.showToastMessage(errorMessage.msg)
                binding.progressBar.visibility = View.GONE
            },
            requestStatus = requestStatus,
            taskStatus = taskStatus,
            slaStatus = slaStatus,
            requestPriority = requestPriority
        )
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
        binding.taskNumber.text = (taskResponse.taskId ?: "").toString()
        binding.taskName.text = taskResponse.taskName ?: ""
        binding.forecastStartDateValue.text =
            taskResponse.forecastStartDate?.let { Utilities.getDateFromISO8601(it) }
        binding.taskPriority.text =
            taskResponse.requestPriority ?: "".also { binding.taskPriority.visibility = View.GONE }
        binding.forecastEndDateValue.text =
            taskResponse.forecastEndDate?.let { Utilities.getDateFromISO8601(it) }
        binding.taskStatusValue.text = taskResponse.taskStatus ?: ""
        binding.slaStatusValue.text = taskResponse.slaStatus ?: ""
        binding.customerSiteIdValue.text = taskResponse.customerSiteId ?: ""
        binding.tawalSiteIdValue.text = taskResponse.siteId ?: ""
        binding.planStartDateValue.text =
            taskResponse.forecastStartDate?.let { Utilities.getDateFromISO8601(it) }
        binding.planEndDateValue.text =
            taskResponse.forecastStartDate?.let { Utilities.getDateFromISO8601(it) }

        binding.requesterValue.text = taskResponse.requester ?: ""
        binding.regionTypeValue.text = taskResponse.region ?: ""
        binding.districtValue.text = taskResponse.district ?: ""
        binding.cityValue.text = taskResponse.city ?: ""

        taskResponse.taskStatus?.let {
            if (it != "Assigned") {
                binding.llStartActivity.visibility = View.GONE
            }
        }
        binding.closeTv.setOnClickListener {
            dialog.dismiss()
        }
        binding.startActivity.setOnClickListener {
            val taskResponseDao = db?.taskResponseDao()
            lifecycleScope.launch(Dispatchers.IO) {
                taskResponseDao?.insertTaskResponse(createTaskResponsePojo(taskResponse))
            }
            val intent = Intent(requireActivity(), StartTaskActivity::class.java)
            intent.putExtra(AppConstants.IntentKeys.taskDetailIntentExtra, taskResponse)
            baseActivity.launchActivityWithIntent(intent)
        }
        taskResponse.taskId?.let { taskId ->
            binding.unAssign.setOnClickListener {
                callUpdateTaskStatusApi(taskId, "unassign")
            }
        }
    }

    private fun createTaskResponseDataClass(taskResponsePOJO: TaskResponsePOJO): TaskResponse {
        return TaskResponse(
            taskId = taskResponsePOJO.taskId,
            requestId = taskResponsePOJO.requestId,
            siteId = taskResponsePOJO.siteId,
            customerSiteId = taskResponsePOJO.customerSiteId,
            taskName = taskResponsePOJO.taskName,
            taskStatus = taskResponsePOJO.taskStatus,
            requestPriority = taskResponsePOJO.requestPriority,
            forecastStartDate = taskResponsePOJO.forecastStartDate,
            forecastEndDate = taskResponsePOJO.forecastEndDate,
            actualStartDate = taskResponsePOJO.actualStartDate,
            slaDuration = taskResponsePOJO.slaDuration,
            slaUnit = taskResponsePOJO.slaUnit,
            processName = taskResponsePOJO.processName,
            processId = taskResponsePOJO.processId,
            requestStatus = taskResponsePOJO.requestStatus,
            slaStatus = taskResponsePOJO.slaStatus,
            requester = taskResponsePOJO.requester,
            region = taskResponsePOJO.region,
            district = taskResponsePOJO.district,
            city = taskResponsePOJO.city
        )
    }

    fun createTaskResponsePojo(taskResponse: TaskResponse): TaskResponsePOJO {
        return TaskResponsePOJO(
            taskResponse.taskId ?: 0,
            taskResponse.requestId,
            taskResponse.siteId,
            taskResponse.customerSiteId,
            taskResponse.taskName,
            taskResponse.taskStatus,
            taskResponse.requestPriority,
            taskResponse.forecastStartDate,
            taskResponse.forecastEndDate,
            taskResponse.actualStartDate,
            taskResponse.slaDuration,
            taskResponse.slaUnit,
            taskResponse.processName,
            taskResponse.processId,
            taskResponse.requestStatus,
            taskResponse.slaStatus,
            taskResponse.requester,
            taskResponse.region,
            taskResponse.district,
            taskResponse.city
        )
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
        binding.savedTasksRv.visibility = if (isProfileVisible) View.VISIBLE else View.GONE
        binding.fieldWorkText.visibility = if (isProfileVisible) View.VISIBLE else View.GONE
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

    private fun callUpdateTaskStatusApi(taskId: Int, taskStatus: String) {
        binding.progressBar.visibility = View.VISIBLE
        // Creating a empty json body
        val json = "{\"key\":\"value\"}"
        val body: RequestBody = RequestBody.create(MediaType.parse("application/json"), json)

        viewModel.updateTaskStatus(
            { successResponse ->
                successResponse?.let {
                    binding.progressBar.visibility = View.GONE
                    it.flag?.let { flag ->
                        if (flag == "0") {
                            baseActivity.showToastMessage("Status Updated Successfully")
                        }
                    }
                }
            },
            { errorMessage ->
                baseActivity.showToastMessage(errorMessage.msg)
                binding.progressBar.visibility = View.GONE
            }, taskId,
            taskStatus,
            body
        )
    }

    private fun showProgressBar() {

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

    private fun setSavedTasksListAdapter(taskResponse: List<TaskResponse>) {
        savedTasksAdapter = SavedTasksAdapter(taskResponse, activity as BaseActivity,
            object : ClickInterfaces.MyTasks {
                override fun myTaskClicked(taskResponse: TaskResponse) {
                    openStartTaskActivityWithRoomData(taskResponse)
                }
            })
        binding.savedTasksRv.layoutManager = LinearLayoutManager(activity)
        binding.savedTasksRv.adapter = savedTasksAdapter
    }

    private fun openStartTaskActivityWithRoomData(taskResponse: TaskResponse) {
        val intent = Intent(requireActivity(), StartTaskActivity::class.java)
        intent.putExtra(AppConstants.IntentKeys.taskDetailIntentExtra, taskResponse)
        intent.putExtra(AppConstants.IntentKeys.taskDetailIntentExtra, taskResponse)
        intent.putExtra(AppConstants.IntentKeys.isStartCalledFromRoom, true)
        baseActivity.launchActivityWithIntent(intent)
    }

}