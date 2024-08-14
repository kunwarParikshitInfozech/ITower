package com.isl.leaseManagement.baladiya.fieldWork.activities.fieldTaskInprogress

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.isl.itower.MyApp
import com.isl.leaseManagement.baladiya.fieldWork.activities.checkList.BaladiyaPermitChecklistActivity
import com.isl.leaseManagement.baladiya.fieldWork.activities.submit.SubmitBaladiyaRequestActivity
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.common.activities.addtionalDocs.AdditionalDocumentsActivity
import com.isl.leaseManagement.common.activities.home.LsmHomeActivity
import com.isl.leaseManagement.dataClasses.responses.SubmitBaladiyaFWRequest
import com.isl.leaseManagement.dataClasses.responses.TaskResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.Utilities
import com.isl.leaseManagement.utils.Utilities.showActionPopup
import infozech.itower.R
import infozech.itower.databinding.ActivityFieldWorkTaskInProgressBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FieldWorkTaskInProgressActivity : BaseActivity() {

    private lateinit var binding: ActivityFieldWorkTaskInProgressBinding
    private lateinit var viewModel: FieldWorkTaskInProgressViewModel
    private var disposable: Disposable? = null
    private var isBaladiyaApplicationSubmitted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_field_work_task_in_progress)
        init()
    }

    private fun init() {
        initViewModel()
        MyApp.localTempVarStore.taskResponse?.let { fillTaskDetailsData(it) }
        setCLickListeners()
        getBaladiyaNameListAndSave()
    }

    override fun onResume() {
        super.onResume()
        getStartTaskDataFromRoom()
    }

    private fun getStartTaskDataFromRoom() {
        val fieldWorkStartDao = commonDatabase.fieldWorkStartDao()
        disposable = fieldWorkStartDao.getFieldWorkStartResponseByID(MyApp.localTempVarStore.taskId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ baladiyaStartResponse ->
                baladiyaStartResponse.data?.baladiyaApplicationSubmitted?.let {
                    isBaladiyaApplicationSubmitted = it == "Yes"
                }
            }, { error -> //
                showToastMessage(error.message.toString())
            })
    }

    private fun getBaladiyaNameListAndSave() {
        showProgressBar()
        viewModel.getBaladiyaName(
            { successResponse ->
                hideProgressBar()
                successResponse?.let { baladiyaNameList ->
                    MyApp.localTempVarStore.baladiyaNameList = baladiyaNameList
//                    lifecycleScope.launch(Dispatchers.IO) {
//                        commonDatabase.baladiyaNameDao().insertAll(baladiyaNameList)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe({
//                                showToastMessage("Drop Down Data Inserted")
//                            }, {
//                                showToastMessage("Drop Down Data insertion failed")
//                                // Handle error
//                            })
//                    }
                }
            },
            { errorMessage ->
                showToastMessage("Unable to get Baladiya Name List!")
                hideProgressBar()
            },
        )
    }

    private fun initViewModel() {
        val factory = FieldWorkTaskInProgressViewModelFactory(FieldWorkTaskInProgressRepository())
        viewModel =
            ViewModelProvider(this, factory).get(FieldWorkTaskInProgressViewModel::class.java)
    }

    private fun setCLickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.submitBaladiyaRequestTv.setOnClickListener {
            launchActivity(
                SubmitBaladiyaRequestActivity::class.java
            )
        }
        binding.baladiyaPermitCheckListTv.setOnClickListener {
            if (isBaladiyaApplicationSubmitted) {
                launchActivity(
                    BaladiyaPermitChecklistActivity::class.java
                )
            } else {
                showToastMessage("Change Baladiya Application submitted status to yes!")
            }
        }
        binding.additionalDocuments.setOnClickListener {
            launchActivity(AdditionalDocumentsActivity::class.java)
        }
        binding.actionBtn.setOnClickListener { showActionPopup(this@FieldWorkTaskInProgressActivity) }

        binding.submitApiBtn.setOnClickListener {
            callSubmitApi()
        }
    }

    private fun callSubmitApi() {
        showProgressBar()
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""
        viewModel.submitBaladiyaFW(
            userId = lsmUserId,
            taskId = MyApp.localTempVarStore.taskId,
            submitBaladiyaFWRequest = SubmitBaladiyaFWRequest(
                processId = MyApp.localTempVarStore.taskResponse?.processId
            ),
            successCallback = { response ->
                hideProgressBar()
                response?.flag?.let {
                    if (it == "0") {
                        showToastMessage("Data Submitted")
                        removeDataFromRoomAfterSubmit()
                    } else {
                        showToastMessage("Unable to submit")
                    }
                }
            }, errorCallback = {
                hideProgressBar()
                showToastMessage("Unable to submit")
            }
        )
    }

    private fun removeDataFromRoomAfterSubmit() {
        val fieldWorkStartDao = commonDatabase.fieldWorkStartDao()
        disposable = fieldWorkStartDao.deleteByTaskId((MyApp.localTempVarStore.taskId).toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                lifecycleScope.launch(Dispatchers.IO) {
                    MyApp.getMyDatabase()
                        .taskResponseDao()    //deleting task response  data
                        .deleteTaskResponseByTaskId(MyApp.localTempVarStore.taskId)

                    delay(100)
                    launchNewActivityCloseAllOther(LsmHomeActivity::class.java)
                }
            }, { error -> //
                showToastMessage(error.message.toString())
            })
    }

    private fun fillTaskDetailsData(taskResponse: TaskResponse) {
        taskResponse.taskStatus
        binding.taskId.text = (taskResponse.siteId ?: "").toString()
        binding.taskName.text = taskResponse.taskName ?: ""
        taskResponse.forecastEndDate?.let {
            binding.dueByValue.text = Utilities.getDateFromISO8601(it)
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

}