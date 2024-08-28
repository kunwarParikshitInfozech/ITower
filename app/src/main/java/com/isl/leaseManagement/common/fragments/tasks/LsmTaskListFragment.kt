package com.isl.leaseManagement.common.fragments.tasks

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
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.base.BaseFragment
import com.isl.leaseManagement.common.activities.filter.FilterTasksActivity
import com.isl.leaseManagement.common.activities.home.LsmHomeActivity
import com.isl.leaseManagement.common.activities.startTask.StartTaskActivity
import com.isl.leaseManagement.common.adapters.SavedTasksAdapter
import com.isl.leaseManagement.common.adapters.TasksAdapter
import com.isl.leaseManagement.dataClasses.responses.TaskResponse
import com.isl.leaseManagement.room.db.MyDatabase
import com.isl.leaseManagement.room.entity.common.TaskResponsePOJO
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities
import com.isl.leaseManagement.utils.Utilities.dpToPx
import com.isl.leaseManagement.utils.Utilities.formatSingleDigitNumber
import infozech.itower.R
import infozech.itower.databinding.FragmentLsmTaskListBinding
import infozech.itower.databinding.TaskDetailsPopupBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody

class LsmTaskListFragment : BaseFragment() {

    private var tasksAdapter: TasksAdapter? = null
    private var savedTasksAdapter: SavedTasksAdapter? = null
    private lateinit var binding: FragmentLsmTaskListBinding
    private val repository = TaskRepository()
    private val viewModel: TaskViewModel by viewModels { TaskViewModelFactory(repository) }
    private lateinit var startActivityLauncher: ActivityResultLauncher<Intent>
    var db: MyDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLsmTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        changeToolBar()
        setClickListeners()
        db = MyApp.getMyDatabase()
        registerActivityLauncher()
        getTaskListData()  //to avoid overriding data of filter data
    }

    override fun onResume() {
        super.onResume()
    }

    private fun getTaskListData() {
        if (LsmHomeActivity.taskIsAssigned) {
            callTasksListApi(taskStatus = "Assigned")
            getSavedTaskList()
        } else {
            callTasksListApi(taskStatus = "Unassigned")
            binding.resumeText.visibility = View.GONE
            binding.scheduledText.visibility = View.GONE
        }
    }

    private fun changeToolBar() {
        val toolbar: View = requireActivity().findViewById(R.id.leaseManagementToolbar)
        val profileLayoutToolbar = toolbar.findViewById<ConstraintLayout>(R.id.profileDutyCl)
        val searchSortLayoutToolbar = toolbar.findViewById<ConstraintLayout>(R.id.searchSortCl)
        profileLayoutToolbar.visibility = View.GONE
        searchSortLayoutToolbar.visibility = View.VISIBLE
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
        val toolbar: View = requireActivity().findViewById(R.id.leaseManagementToolbar)
        val toolbarBackIv = toolbar.findViewById<ImageView>(R.id.toolbarBackIv)
        toolbarBackIv.setOnClickListener {
            backClicked()
        }
        val toolbarFilter = toolbar.findViewById<ImageView>(R.id.toolbarFilterIv)
        toolbarFilter.setOnClickListener {
            val intent =
                Intent(context, FilterTasksActivity::class.java)
            startActivityLauncher.launch(intent)
        }
    }

    private fun backClicked() {
        LsmHomeActivity.myFragmentManager?.let { it1 ->
            LsmHomeActivity.openFragment(
                LsmTasksFragment(),
                it1
            )
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
                    binding.scheduledText.text =
                        "Schedule (${formatSingleDigitNumber(taskList.size.toString())})"
                } else {
                    baseActivity.showToastMessage("No task found!")
                    binding.scheduledText.text = "Schedule (00)"
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
        binding.taskNumber.text = (taskResponse.siteId ?: "").toString()
        binding.taskName.text = taskResponse.taskName ?: ""
        binding.forecastStartDateValue.text =
            taskResponse.forecastStartDate?.let { Utilities.getDateFromISO8601(it) }
        binding.taskPriority.text =
            taskResponse.requestPriority ?: "".also { binding.taskPriority.visibility = View.GONE }
        if (taskResponse.requestPriority != null && taskResponse.requestPriority == "Low") {
            binding.taskPriority.setTextColor(baseActivity.getColor(R.color.color_34C759))
            binding.taskPriority.background =
                baseActivity.getDrawable(R.drawable.rounded_bg_tv_green)
        }
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
        binding.siteTowerTypeValue.text = taskResponse.towerType ?: ""

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
            dialog.dismiss()
            baseActivity.launchActivityWithIntent(intent)
        }
        taskResponse.taskId?.let { taskId ->
            binding.unAssign.setOnClickListener {
                callUpdateTaskStatusApi(taskId, "unassign", dialog)
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
            city = taskResponsePOJO.city,
            towerType = taskResponsePOJO.towerType
        )
    }

    private fun createTaskResponsePojo(taskResponse: TaskResponse): TaskResponsePOJO {
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
            taskResponse.city,
            taskResponse.towerType
        )
    }

    private fun callUpdateTaskStatusApi(taskId: Int, taskStatus: String, dialog: Dialog) {
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
                            callTasksListApi()
                            dialog.dismiss()
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
        binding.progressBar.visibility = View.VISIBLE
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
        lifecycleScope.launch(Dispatchers.Main) {
            savedTasksAdapter = SavedTasksAdapter(taskResponse, activity as BaseActivity,
                object : ClickInterfaces.MyTasks {
                    override fun myTaskClicked(taskResponse: TaskResponse) {
                        openStartTaskActivityWithRoomData(taskResponse)
                    }
                })
            binding.savedTasksRv.layoutManager = LinearLayoutManager(activity)
            binding.savedTasksRv.adapter = savedTasksAdapter
            binding.resumeText.text =
                "Resume (${formatSingleDigitNumber(taskResponse.size.toString())})"
        }
    }

    private fun openStartTaskActivityWithRoomData(taskResponse: TaskResponse) {
        val intent = Intent(requireActivity(), StartTaskActivity::class.java)
        intent.putExtra(AppConstants.IntentKeys.taskDetailIntentExtra, taskResponse)
        intent.putExtra(AppConstants.IntentKeys.isStartCalledFromRoom, true)
        baseActivity.launchActivityWithIntent(intent)
    }

}