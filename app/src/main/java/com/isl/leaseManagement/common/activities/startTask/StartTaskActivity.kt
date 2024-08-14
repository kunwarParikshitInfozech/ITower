package com.isl.leaseManagement.common.activities.startTask

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.isl.itower.MyApp
import com.isl.leaseManagement.baladiya.fieldWork.activities.fieldTaskInprogress.FieldWorkTaskInProgressActivity
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.common.activities.home.LsmHomeActivity
import com.isl.leaseManagement.dataClasses.requests.StartTaskRequest
import com.isl.leaseManagement.dataClasses.responses.FieldWorkStartTaskResponse
import com.isl.leaseManagement.dataClasses.responses.PaymentStartTaskResponse
import com.isl.leaseManagement.dataClasses.responses.TaskResponse
import com.isl.leaseManagement.paymentProcess.activities.taskInProgress.PaymentTaskInProgressActivity
import com.isl.leaseManagement.room.db.MyDatabase
import com.isl.leaseManagement.room.entity.paymentProcess.StartTaskPaymentPOJO
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.AppConstants.IntentKeys.isStartCalledFromRoom
import infozech.itower.R
import infozech.itower.databinding.ActivityGetStartDataBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StartTaskActivity : BaseActivity() {                   // all lsm modules are common till here
    private lateinit var binding: ActivityGetStartDataBinding
    private lateinit var viewModel: StartTaskViewModel
    private var taskResponse: TaskResponse? = null
    private var paymentDatabase: MyDatabase? = null;
    private var currentTaskId = 0
    private var isCalledFromSavedTaskList = false
    private var processId = 0
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_start_data)
        init()
    }

    private fun init() {
        paymentDatabase = MyApp.getMyDatabase()
        val factory = StartTaskViewModelFactory(StartTaskRepository())
        viewModel = ViewModelProvider(this, factory).get(StartTaskViewModel::class.java)
        taskResponse =
            intent.getSerializableExtra(AppConstants.IntentKeys.taskDetailIntentExtra) as TaskResponse?

        if (taskResponse == null || taskResponse!!.processId == null) {
            showToastMessage("Process ID is empty!")
            launchNewActivityCloseAllOther(LsmHomeActivity::class.java)
        }
        if (taskResponse?.processId == null || taskResponse!!.processId == 0) {
            showToastMessage("Process Id is empty!")
            finish()
        }
        processId = taskResponse!!.processId!!

        val currentTaskId = taskResponse?.taskId
        if (currentTaskId != null) {
            this.currentTaskId = currentTaskId
            MyApp.localTempVarStore.taskId = currentTaskId
            MyApp.localTempVarStore.taskResponse = taskResponse

            isCalledFromSavedTaskList = intent.getBooleanExtra(isStartCalledFromRoom, false)
            if (isCalledFromSavedTaskList) {
                getStartDataFromRoom(currentTaskId)
            } else {
                callStartTaskApis(currentTaskId)
            }
        } else {
            showToastMessage("Task ID is empty!")
            return
        }
        setClickListeners()
    }

    private fun getStartDataFromRoom(currentTaskId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (processId == AppConstants.ProcessIds.paymentProcess) {   //payment
                paymentDatabase?.startTaskDao()?.getStartTaskById(currentTaskId)
                    ?.let { startTaskPOJO ->
                        proceedToPaymentTaskInProgress(convertStartTaskPOJOtoDataClass(startTaskPOJO))
                    }
            } else if (processId == AppConstants.ProcessIds.baladiyaFieldWork) {  //baladiya field work
                disposable = commonDatabase.fieldWorkStartDao().getFieldWorkStartResponseByID(currentTaskId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ baladiyaStartResponse ->   //from room
                        proceedToFieldWorkTaskInProgress(baladiyaStartResponse)
                    }, { error ->//
                        showToastMessage(error.message.toString())
                    })
            }
        }
    }

    private fun proceedToFieldWorkTaskInProgress(fieldWorkStartTaskResponse: FieldWorkStartTaskResponse) {
        fieldWorkStartTaskResponse.taskId = currentTaskId
        val d = commonDatabase.fieldWorkStartDao().insert(response = fieldWorkStartTaskResponse)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {
                // Handle error
            })

        //saving irrespective of if data fetched from room or api as even with room, only same data will override so not an issue
        launchActivity(FieldWorkTaskInProgressActivity::class.java)
    }

    private fun convertStartTaskPOJOtoDataClass(pojo: StartTaskPaymentPOJO): PaymentStartTaskResponse {
        val dataJson = pojo.dataJson
        val processId = pojo.processId
        val gson = Gson()
        val startTaskData: PaymentStartTaskResponse.StartTaskData? = if (dataJson.isEmpty()) {
            null
        } else {
            gson.fromJson(dataJson, PaymentStartTaskResponse.StartTaskData::class.java)
        }
        return PaymentStartTaskResponse(startTaskData, processId)
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { launchNewActivityCloseAllOther(LsmHomeActivity::class.java) }
    }

    private fun callStartTaskApis(currentTaskId: Int) {
        when (processId) {
            AppConstants.ProcessIds.paymentProcess -> {
                callStartTaskAPIForPayment(currentTaskId)
            }

            AppConstants.ProcessIds.baladiyaFieldWork -> {
                callStartTaskAPIForFieldWork(currentTaskId)
            }
        }
    }

    private fun callStartTaskAPIForPayment(currentTaskId: Int) {
        showProgressBar()
        val startTaskRequest = StartTaskRequest(
            latitude = 0,
            longitude = 0,
            requestId = "",
            source = "",
            timestamp = ""
        )
        viewModel.startTaskForPayment(
            {  hideProgressBar()
                if (it?.processId == null) {
                    showToastMessage("Process ID is empty!")
                    finish()
                }
                when (it?.processId) {
                    AppConstants.ProcessIds.paymentProcess -> {
                        proceedToPaymentTaskInProgress(it)
                    }
                }
            },
            { errorMessage ->
                hideProgressBar()
                showToastMessage("Unable to start Activity")
                launchNewActivityCloseAllOther(LsmHomeActivity::class.java)
            }, taskId = currentTaskId,
            body =
            startTaskRequest
        )
    }

    private fun callStartTaskAPIForFieldWork(currentTaskId: Int) {
        showProgressBar()
        val startTaskRequest = StartTaskRequest(
            latitude = 0,
            longitude = 0,
            requestId = "",
            source = "",
            timestamp = ""
        )
        viewModel.startTaskForFieldWork(
            {  hideProgressBar()
                if (it?.processId == null) {
                    showToastMessage("Process ID is empty!")
                    finish()
                }
                when (it?.processId) {
                    AppConstants.ProcessIds.baladiyaFieldWork -> {
                        showToastMessage("From Api")
                        proceedToFieldWorkTaskInProgress(it)
                    }
                }
            },
            { errorMessage ->
                hideProgressBar()
                showToastMessage("Unable to start Activity")
                launchNewActivityCloseAllOther(LsmHomeActivity::class.java)
            }, taskId = currentTaskId,
            body =
            startTaskRequest
        )
    }

    private fun proceedToPaymentTaskInProgress(startTask: PaymentStartTaskResponse?) {
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
                    PaymentTaskInProgressActivity::class.java
                )
                intent.putExtra(
                    AppConstants.IntentKeys.taskDetailIntentExtra,
                    taskResponse
                )
                if (response.data.shouldUpdateSubmitDocFromStart != 2) {
                    response.data.shouldUpdateSubmitDocFromStart = 1
                }  //for first time allowing doc to be saved from start task
                MyApp.localTempVarStore.paymentStartTaskResponse = response
                response.processId?.let {
                    if (it != 3) {
                        showToastMessage("Process ID is not of Payment Process")
                        return
                    }
                }
                if (!isCalledFromSavedTaskList) { //saving only when data is fetched from API and not from Room
                    val startTaskPojo = convertToStartTaskResponsePOJO(response)
                    val startTaskDao = paymentDatabase?.startTaskDao()
                    lifecycleScope.launch(Dispatchers.IO) {
                        startTaskDao?.insertStartTask(startTaskPojo)
                    }
                }
                finish()
                launchActivityWithIntent(intent)
            }
        }
    }

    private fun convertToStartTaskResponsePOJO(paymentStartTaskResponse: PaymentStartTaskResponse): StartTaskPaymentPOJO {
        val gson = Gson()
        val dataJson = gson.toJson(paymentStartTaskResponse.data)
        return StartTaskPaymentPOJO(
            currentTaskId,
            dataJson,
            paymentStartTaskResponse.processId
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