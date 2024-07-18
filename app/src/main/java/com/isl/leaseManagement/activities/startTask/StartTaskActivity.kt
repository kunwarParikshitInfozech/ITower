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
import infozech.itower.R
import infozech.itower.databinding.ActivityGetStartDataBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StartTaskActivity : BaseActivity() {
    private lateinit var binding: ActivityGetStartDataBinding
    private lateinit var viewModel: StartTaskViewModel
    private var taskResponse: TaskResponse? = null
    var db: MyDatabase? = null;

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
        taskId?.let {
            MyApp.localTempVarStore.taskId = it
            MyApp.localTempVarStore.taskResponse = taskResponse
            callStartTaskApi(taskId)
        }
        setClickListeners()
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
            { successResponse ->
                successResponse?.let { response ->
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
                                return@startTask
                            }
                        }
                        val startTaskPojo = convertToStartTaskResponse(response)
                        val startTaskDao = db?.startTaskDao()
                        lifecycleScope.launch(Dispatchers.IO) {
                            startTaskDao?.insertStartTask(startTaskPojo)
                        }
                        finish()
                        launchActivityWithIntent(intent)
                    }
                }
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

    fun convertToStartTaskResponse(startTaskResponse: StartTaskResponse): StartTaskResponsePOJO {
        val gson = Gson()
        val dataJson = gson.toJson(startTaskResponse.data)
        val startResponsePojo =
            StartTaskResponsePOJO(
                0, // Provide an appropriate taskId
                dataJson,
                startTaskResponse.processId
            )
        return startResponsePojo
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