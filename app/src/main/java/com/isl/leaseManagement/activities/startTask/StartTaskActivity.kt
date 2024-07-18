package com.isl.leaseManagement.activities.startTask

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.isl.itower.MyApp
import com.isl.leaseManagement.activities.home.LsmHomeActivity
import com.isl.leaseManagement.activities.taskInProgress.TaskInProgressActivity
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClass.requests.StartTaskRequest
import com.isl.leaseManagement.dataClass.responses.TaskResponse
import com.isl.leaseManagement.utils.AppConstants
import infozech.itower.R
import infozech.itower.databinding.ActivityGetStartDataBinding

class StartTaskActivity : BaseActivity() {
    private lateinit var binding: ActivityGetStartDataBinding
    private lateinit var viewModel: StartTaskViewModel
    private var taskResponse: TaskResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_start_data)
        init()
    }

    private fun init() {
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