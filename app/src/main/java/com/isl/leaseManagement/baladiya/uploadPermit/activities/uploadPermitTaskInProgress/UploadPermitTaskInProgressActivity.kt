package com.isl.leaseManagement.baladiya.uploadPermit.activities.uploadPermitTaskInProgress

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.isl.itower.MyApp
import com.isl.leaseManagement.baladiya.fieldWork.activities.fieldTaskInprogress.FieldWorkTaskInProgressRepository
import com.isl.leaseManagement.baladiya.fieldWork.activities.fieldTaskInprogress.FieldWorkTaskInProgressViewModel
import com.isl.leaseManagement.baladiya.fieldWork.activities.fieldTaskInprogress.FieldWorkTaskInProgressViewModelFactory
import com.isl.leaseManagement.baladiya.uploadPermit.activities.uploadPermitCertificate.UploadBaladiyaPermitActivity
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.common.activities.addtionalDocs.AdditionalDocumentsActivity
import com.isl.leaseManagement.common.activities.home.LsmHomeActivity
import com.isl.leaseManagement.dataClasses.responses.SubmitBaladiyaFWRequest
import com.isl.leaseManagement.dataClasses.responses.TaskResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.ActionButtonMethods
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.Utilities
import infozech.itower.R
import infozech.itower.databinding.ActivityUploadPermitTaskInProgressBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadPermitTaskInProgressActivity : BaseActivity() {

    private lateinit var binding: ActivityUploadPermitTaskInProgressBinding
    private lateinit var viewModel: FieldWorkTaskInProgressViewModel
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_upload_permit_task_in_progress)
        init()
    }

    private fun init() {
        initViewModel()
        MyApp.localTempVarStore.taskResponse?.let { fillTaskDetailsData(it) }
        setCLickListeners()
    }

    private fun initViewModel() {
        val factory = FieldWorkTaskInProgressViewModelFactory(FieldWorkTaskInProgressRepository())
        viewModel =
            ViewModelProvider(this, factory).get(FieldWorkTaskInProgressViewModel::class.java)
    }

    private fun setCLickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.uploadPermitBtn.setOnClickListener {
            launchActivity(
                UploadBaladiyaPermitActivity::class.java
            )
        }
        binding.additionalDocuments.setOnClickListener {
            launchActivity(AdditionalDocumentsActivity::class.java)
        }
        binding.actionBtn.setOnClickListener {    ActionButtonMethods.Actions.showActionPopup(this,
            ActionButtonMethods.ActionOpeningProcess.PaymentAndBaladiya
        ) }

        binding.submitApiBtn.setOnClickListener {
            checkIfBaladiyaCertificateUploadedAndCallSubmitApi()
        }
    }

    private fun checkIfBaladiyaCertificateUploadedAndCallSubmitApi() {
        disposable = commonDatabase.fieldWorkStartDao()
            .getFieldWorkStartResponseByID(MyApp.localTempVarStore.taskId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response ->     //from room
                response.data?.let {
                    var isBaladiyaCertificateUploaded = false
                    it.documents?.let { docs ->
                        for (doc in docs) {
                            if (doc?.tagName?.equals(AppConstants.DocsTagNames.fieldWorkBaladiyaCertificate) == true
                                && doc.content != null && doc.content != ""
                            ) {
                                isBaladiyaCertificateUploaded = true
                            }
                        }
                    }
                    if (isBaladiyaCertificateUploaded) {
                        getAdditionalDocsAndCallSubmitApi()
                    } else {
                        showToastMessage("Upload Baladiya Permit Certificate!")
                    }
                }
            }
    }


    private var additionalDocIds = mutableListOf<String>()
    private fun getAdditionalDocsAndCallSubmitApi() {
        lifecycleScope.launch(Dispatchers.IO) {
            val documentsList =
                MyApp.getMyDatabase().saveAdditionalDocumentDao()
                    .getAllSavedDocumentsOfATask(MyApp.localTempVarStore.taskId.toString())
            documentsList?.let { list ->
                for (item in list) {
                    if (!item.documentUploadedToAPI) {  //if not uploaded , only than we will save these IDs
                        item.docUploadId?.let {
                            additionalDocIds.add(it)
                        }
                    }

                }
                withContext(Dispatchers.Main) {
                    callSubmitApi()
                }
            }
        }
    }

    private fun callSubmitApi() {
        showProgressBar()
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""
        val additionalDocuments = mutableListOf<SubmitBaladiyaFWRequest.AdditionalDocument?>()
        for (docId in additionalDocIds) {
            additionalDocuments.add(SubmitBaladiyaFWRequest.AdditionalDocument(docId))
        }
        viewModel.submitBaladiyaFW(
            userId = lsmUserId,
            taskId = MyApp.localTempVarStore.taskId,
            submitBaladiyaFWRequest = SubmitBaladiyaFWRequest(
                processId = MyApp.localTempVarStore.taskResponse?.processId,
                requestId = MyApp.localTempVarStore.taskResponse?.requestId,
                additionalDocs = additionalDocuments
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
                    delay(50)
                    MyApp.getMyDatabase().saveAdditionalDocumentDao()
                        .deleteAllDocumentsOfTask(MyApp.localTempVarStore.taskId)
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