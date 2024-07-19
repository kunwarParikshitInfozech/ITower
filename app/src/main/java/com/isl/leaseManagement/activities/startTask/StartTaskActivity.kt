package com.isl.leaseManagement.activities.startTask

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.isl.itower.MyApp
import com.isl.leaseManagement.activities.home.LsmHomeActivity
import com.isl.leaseManagement.activities.taskInProgress.TaskInProgressActivity
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClass.requests.StartTaskRequest
import com.isl.leaseManagement.dataClass.responses.StartTaskResponse
import com.isl.leaseManagement.dataClass.responses.TaskResponse
import com.isl.leaseManagement.room.db.MyDatabase
import com.isl.leaseManagement.room.entity.StartTaskResponsePOJO
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.AppConstants.IntentKeys.isStartCalledFromRoom
import infozech.itower.R
import infozech.itower.databinding.ActivityGetStartDataBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StartTaskActivity : BaseActivity() {
    private lateinit var binding: ActivityGetStartDataBinding
    private lateinit var viewModel: StartTaskViewModel
    private var taskResponse: TaskResponse? = null
    var db: MyDatabase? = null;
    private var taskId = 0
    private var isCalledFromSavedTaskList = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_start_data)
        init()
    }

    private fun init() {
        db = MyApp.getMyDatabase()
        val factory = StartTaskViewModelFactory(StartTaskRepository())
        viewModel = ViewModelProvider(this, factory).get(StartTaskViewModel::class.java)
        taskResponse =
            intent.getSerializableExtra(AppConstants.IntentKeys.taskDetailIntentExtra) as TaskResponse?
        val taskId = taskResponse?.taskId
        if (taskId != null) {
            this.taskId = taskId
            MyApp.localTempVarStore.taskId = taskId
            MyApp.localTempVarStore.taskResponse = taskResponse

            isCalledFromSavedTaskList = intent.getBooleanExtra(isStartCalledFromRoom, false)
            if (isCalledFromSavedTaskList) {
                getStartDataFromRoom(taskId)
                showToastMessage("Starting from room")
            } else {
                callStartTaskApi(taskId)
                showToastMessage("Starting from API")
            }
        } else {
            showToastMessage("Task ID is empty!")
            return
        }
        setClickListeners()
    }

    private fun getStartDataFromRoom(taskId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            db?.startTaskDao()?.getStartTaskById(taskId)?.let { startTaskPOJO ->
                proceedWithStartTaskResponse(convertStartTaskPOJOtoDataClass(startTaskPOJO))
            }
        }
    }

    private fun convertStartTaskPOJOtoDataClass(pojo: StartTaskResponsePOJO): StartTaskResponse {
        val dataJson = pojo.dataJson
        val processId = pojo.processId
        val gson = Gson()
        val startTaskData: StartTaskResponse.StartTaskData? = if (dataJson.isEmpty()) {
            null
        } else {
            gson.fromJson(dataJson, StartTaskResponse.StartTaskData::class.java)
        }
        return StartTaskResponse(startTaskData, processId)
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { launchNewActivityCloseAllOther(LsmHomeActivity::class.java) }
    }

    private fun callStartTaskApi(taskId: Int) {
        showProgressBar()
        val startTaskRequest = StartTaskRequest(
            latitude = 0,
            longitude = 0,
            requestId = "",
            source = "",
            timestamp = ""
        )
        viewModel.startTask(
            {
                proceedWithStartTaskResponse(it)
            },
            { errorMessage ->
                hideProgressBar()
                showToastMessage("Unable to start Activity")
                launchNewActivityCloseAllOther(LsmHomeActivity::class.java)
            }, taskId = taskId,
            body =
            startTaskRequest
        )
    }

    private fun proceedWithStartTaskResponse(startTask: StartTaskResponse?) {
        if (startTask == null) {
            showToastMessage("Start Data is empty")
            return
        }
        startTask.let { response ->
            hideProgressBar()
            response.data?.let {
                //got success
                val intent = Intent(
                    this@StartTaskActivity,
                    TaskInProgressActivity::class.java
                )
                intent.putExtra(
                    AppConstants.IntentKeys.taskDetailIntentExtra,
                    taskResponse
                )
                MyApp.localTempVarStore.startTaskResponse = response
                response.processId?.let {
                    if (it != 3) {
                        showToastMessage("Process ID is not of Payment Process")
                        return
                    }
                }
                if (!isCalledFromSavedTaskList) {  //saving only when data is fetched from API and not from Room
                    val startTaskPojo = convertToStartTaskResponsePOJO(response)
                    val startTaskDao = db?.startTaskDao()
                    lifecycleScope.launch(Dispatchers.IO) {
                        startTaskDao?.insertStartTask(startTaskPojo)
                    }
                }
                finish()
                launchActivityWithIntent(intent)
            }
        }
    }

    private fun convertToStartTaskResponsePOJO(startTaskResponse: StartTaskResponse): StartTaskResponsePOJO {
        val gson = Gson()
        val dataJson = gson.toJson(startTaskResponse.data)
        return StartTaskResponsePOJO(
            taskId,
            dataJson,
            startTaskResponse.processId
        )
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        launchNewActivityCloseAllOther(LsmHomeActivity::class.java)
    }

}