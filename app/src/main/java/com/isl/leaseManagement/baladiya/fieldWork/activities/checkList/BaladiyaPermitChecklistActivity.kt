package com.isl.leaseManagement.baladiya.fieldWork.activities.checkList

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.isl.itower.MyApp
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.common.fragments.uploadDoc.UploadSingleDocumentFragment
import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.dataClasses.responses.FieldWorkStartTaskResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities
import infozech.itower.R
import infozech.itower.databinding.ActivityBaladiyaPermitChecklistBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class BaladiyaPermitChecklistActivity : BaseActivity() {

    private lateinit var binding: ActivityBaladiyaPermitChecklistBinding
    private var disposable: Disposable? = null

    private var provideEvidenceDoc =
        MutableLiveData(SaveAdditionalDocument(taskId = MyApp.localTempVarStore.taskId))

    private var sadadDocument =
        MutableLiveData(SaveAdditionalDocument(taskId = MyApp.localTempVarStore.taskId))

    private lateinit var viewModel: BaladiyaPermitChecklistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_baladiya_permit_checklist)
        init()
    }

    private fun init() {
        initViewModel()
        setTagNameToDocuments()
        initializeFollowupCompletedSpinner()
        initializePermitAcquiredSpinner()
        showDocumentUploadFragUI()
        getStartTaskData()
        setClickListeners()
        baladiyaPermitRejected()  // by default everything is disabled
    }

    private fun initViewModel() {
        val factory = BaladiyaPermitChecklistViewModelFactory(BaladiyapermitChecklistRepository())
        viewModel =
            ViewModelProvider(this, factory)[BaladiyaPermitChecklistViewModel::class.java]
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.saveAsDraftBtn.setOnClickListener {
            getDataFromRoomUpdateItAndSave(
                saveToApiAsWell = false,
                shouldShowPhoneSaveSuccessToast = true
            )
        }
        binding.saveToApiAndDraft.setOnClickListener {
            getDataFromRoomUpdateItAndSave(
                saveToApiAsWell = true, shouldShowPhoneSaveSuccessToast = false
            )
        }
        binding.actionBtn.setOnClickListener { Utilities.showActionPopup(this) }
    }

    private fun getDataFromRoomUpdateItAndSave(
        saveToApiAsWell: Boolean,
        shouldShowPhoneSaveSuccessToast: Boolean
    ) {
        disposable = commonDatabase.fieldWorkStartDao()
            .getFieldWorkStartResponseByID(MyApp.localTempVarStore.taskId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->     //from room
                response.data?.let {
                    it.followupWithBaladiyaCompleted =
                        binding.isFollowUpCompletedSpinner.selectedItem.toString()
                    it.baladiyaPermitAcquired =
                        if (binding.isTheBaladiyaPermitAcquiredSpinner.selectedItem?.toString() != "Choose an option") {
                            binding.isTheBaladiyaPermitAcquiredSpinner.selectedItem.toString()
                        } else {
                            ""
                        }
                    it.sadadBillerCode =
                        (binding.sadadBillerCodeValue.text.toString()).toIntOrNull()
                    it.accountNumber = binding.accountNumberValue.text.toString()
                    it.paymentPeriodDays = (binding.paymentDaysValue.text.toString()).toIntOrNull()

                    val documents: MutableList<SaveAdditionalDocument?> =
                        mutableListOf()
                    provideEvidenceDoc.value?.let { doc ->
                        if (doc.tagName != null && doc.tagName != "") {
                            documents.add(doc)
                        }

                    }
                    sadadDocument.value?.let { doc ->
                        if (doc.tagName != null && doc.tagName != "") {
                            documents.add(doc)
                        }
                    }

                    response.data.documents = documents

                    saveBackToRoom(
                        response,
                        saveToApiAsWell,
                        shouldShowPhoneSaveSuccessToast = shouldShowPhoneSaveSuccessToast
                    )
                }
            }, { error ->//
                showToastMessage(error.message.toString())
            })
    }


    private fun saveBackToRoom(
        fieldWorkResponse: FieldWorkStartTaskResponse,
        saveToApiAsWell: Boolean,
        shouldShowPhoneSaveSuccessToast: Boolean
    ) {
        val d = commonDatabase.fieldWorkStartDao().insert(response = fieldWorkResponse)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (shouldShowPhoneSaveSuccessToast) {
                    showToastMessage("Saved Successfully to Phone!")
                }
                if (saveToApiAsWell) {
                    saveResponseToBaladiyaApi(fieldWorkResponse)
                }
            }, {
                // Handle error
            })
    }

    private fun saveResponseToBaladiyaApi(fieldWorkResponse: FieldWorkStartTaskResponse) {
        if (binding.isFollowUpCompletedSpinner.selectedItem.toString() == "Yes" && binding.isTheBaladiyaPermitAcquiredSpinner.selectedItem.toString()
                .contains("oose")
        ) {   //still choose an option is selected, user should select approved or rejected to proceed further
            showToastMessage("Select Permit Acquired")
            return
        }
        if (binding.isTheBaladiyaPermitAcquiredSpinner.selectedItem.toString() == "Approved") {  // need sadad docs and other validation
            if (binding.sadadBillerCodeValue.text.toString() == "") {
                showToastMessage("Enter Sadad Biller Code!")
                return
            }
            if (sadadDocument.value?.docId == null || sadadDocument.value?.docId == "") {
                showToastMessage("Select SADAD Document")
                return
            }
        } else if (binding.isTheBaladiyaPermitAcquiredSpinner.selectedItem.toString() == "Rejected") {  // only need evidence document
            if (provideEvidenceDoc.value?.docId == null || provideEvidenceDoc.value?.docId == "") {
                showToastMessage("Select Provide Evidence Document")
                return
            }
        }
        fieldWorkResponse.additionalDocuments =
            null   // to avoid uploading it here as it cause error due to old doc

        fieldWorkResponse.data?.taskFlag = AppConstants.TaskFlags.taskBaladiyaCheckList

        val newResponseForApi =
            fieldWorkResponse.copy()  //creating new to avoid sending already uploaded document

        val updatedDocumentList = fieldWorkResponse.data?.documents?.filterNotNull()
            ?.filter { !it.isDocumentUploadedToAPI }
            ?.toMutableList() ?: mutableListOf()
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""

        newResponseForApi.data?.documents = updatedDocumentList

        showProgressBar()
        viewModel.updateBaladiyaResponse(
            userId = lsmUserId,
            taskId = MyApp.localTempVarStore.taskId,
            fieldWorkStartTaskResponse = newResponseForApi,
            { response ->
                hideProgressBar()
                response?.flag?.let {
                    if (it == "0") {
                        fieldWorkResponse.data?.isSecondFormSubmittedOnFieldWork =
                            true  //updated that now 2nd form is submitted

                        fieldWorkResponse.data?.documents?.let { docs ->
                            for ((index) in docs.withIndex()) {
                                fieldWorkResponse.data.documents?.get(index)?.isDocumentUploadedToAPI =
                                    true  //to avoid uploading doc when user comes back to this screen
                                if (docs[index]?.tagName == AppConstants.DocsTagNames.fieldWorkProvideEvidence) {
                                    provideEvidenceDoc.value?.isDocumentUploadedToAPI = true
                                }
                                if (docs[index]?.tagName == AppConstants.DocsTagNames.fieldWorkSadadDocument) {
                                    sadadDocument.value?.isDocumentUploadedToAPI = true
                                }
                            }
                        }

                        saveBackToRoom(
                            fieldWorkResponse,
                            false,
                            shouldShowPhoneSaveSuccessToast = false
                        )
                        showToastMessage("Data Saved To API")
                    } else {
                        showToastMessage("Unable to save to API")
                    }
                }
            }, {
                hideProgressBar()
                showToastMessage("Unable to save to API")
            }
        )

    }

    private fun setTagNameToDocuments() {
        provideEvidenceDoc.value?.tagName =
            AppConstants.DocsTagNames.fieldWorkProvideEvidence   // will use this to tag name on upload doc fragment to disable/enable document upload based on null or not,and will not save the doc if null in room or api
        sadadDocument.value?.tagName = AppConstants.DocsTagNames.fieldWorkSadadDocument
    }

    private fun showDocumentUploadFragUI() {
        provideEvidenceDoc.value?.documentTypeName =
            AppConstants.DocTypeNames.provideEvidence
        setFragmentForUploadDoc(binding.uploadDocProvideEvidence, provideEvidenceDoc)

        sadadDocument.value?.documentTypeName =
            AppConstants.DocTypeNames.sadadDocument
        setFragmentForUploadDoc(binding.uploadDocSadadDocument, sadadDocument)
    }

    private fun initializeFollowupCompletedSpinner() {
        Utilities.initializeDropDownWithStringAndIdArray(context = this,
            stringsArray = resources.getStringArray(R.array.yes_no_drop_down),
            spinner = binding.isFollowUpCompletedSpinner,
            commonInterface = object :
                ClickInterfaces.CommonInterface {
                override fun triggerWithString(string: String) {
                    if (string == "Yes") {
                        binding.isTheBaladiyaPermitAcquiredSpinner.isEnabled = true
                        binding.isTheBaladiyaPermitAcquiredSpinner.background =
                            ContextCompat.getDrawable(
                                this@BaladiyaPermitChecklistActivity,
                                R.drawable.btn_light_grey_border_bg
                            )
                    } else {
                        baladiyaPermitRejected()  //to disable all rest of the fields
                        provideEvidenceDoc.value?.tagName = null
                        showDocumentUploadFragUI()   // as this does not get disabled even in rejected case
                        binding.isTheBaladiyaPermitAcquiredSpinner.setSelection(0)   //set select an option and disable the spinner
                        binding.isTheBaladiyaPermitAcquiredSpinner.isEnabled = false
                        binding.isTheBaladiyaPermitAcquiredSpinner.background =
                            ContextCompat.getDrawable(
                                this@BaladiyaPermitChecklistActivity,
                                R.drawable.bg_filled_grey_rounded
                            )
                    }
                }

                override fun triggerWithInt(int: Int) {//no use
                }
            }
        )
    }

    private fun initializePermitAcquiredSpinner() {
        Utilities.initializeDropDownWithStringAndIdArray(context = this,
            stringsArray = resources.getStringArray(R.array.approved_rejected_dropdown),
            spinner = binding.isTheBaladiyaPermitAcquiredSpinner,
            commonInterface = object :
                ClickInterfaces.CommonInterface {
                override fun triggerWithString(string: String) {//no use
                    if (string == "Approved") {
                        baladiyaPermitApproved()
                    } else {
                        baladiyaPermitRejected()
                    }
                }

                override fun triggerWithInt(int: Int) {//no use
                }
            }
        )
    }

    private fun baladiyaPermitApproved() {
        binding.sadadBillerCodeValue.isEnabled = true
        binding.sadadBillerCodeValue.background =
            ContextCompat.getDrawable(this, R.drawable.btn_light_grey_border_bg)
        binding.accountNumberValue.isEnabled = true
        binding.accountNumberValue.background =
            ContextCompat.getDrawable(this, R.drawable.btn_light_grey_border_bg)
        binding.paymentDaysValue.isEnabled = true
        binding.paymentDaysValue.background =
            ContextCompat.getDrawable(this, R.drawable.btn_light_grey_border_bg)

        setTagNameToDocuments()  // enabled doc upload
        provideEvidenceDoc.value?.tagName = null   // to disable provide evidence
        showDocumentUploadFragUI()
    }

    private fun baladiyaPermitRejected() {

        binding.sadadBillerCodeValue.setText("")
        binding.sadadBillerCodeValue.isEnabled = false
        binding.sadadBillerCodeValue.background =
            ContextCompat.getDrawable(this, R.drawable.bg_filled_grey_rounded)

        binding.accountNumberValue.setText("")
        binding.accountNumberValue.isEnabled = false
        binding.accountNumberValue.background =
            ContextCompat.getDrawable(this, R.drawable.bg_filled_grey_rounded)

        binding.paymentDaysValue.setText("")
        binding.paymentDaysValue.isEnabled = false
        binding.paymentDaysValue.background =
            ContextCompat.getDrawable(this, R.drawable.bg_filled_grey_rounded)
        setTagNameToDocuments()
        sadadDocument.value?.tagName = null   // only permit doc will work, not sadad
        showDocumentUploadFragUI()
    }

    private fun setFragmentForUploadDoc(
        fragmentLayout: FrameLayout,
        document: MutableLiveData<SaveAdditionalDocument>
    ) {
        val fragment: Fragment =
            UploadSingleDocumentFragment(document, object : ClickInterfaces.CommonInterface {
                override fun triggerWithString(string: String) {  //for saving deleted data instance
                    getDataFromRoomUpdateItAndSave(false, shouldShowPhoneSaveSuccessToast = true)
                }

                override fun triggerWithInt(int: Int) {
                    //no need
                }

            })
        supportFragmentManager.beginTransaction()
            .replace(fragmentLayout.id, fragment)
            .commit()
    }

    private fun getStartTaskData() {
        val fieldWorkStartDao = commonDatabase.fieldWorkStartDao()
        disposable = fieldWorkStartDao.getFieldWorkStartResponseByID(MyApp.localTempVarStore.taskId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ baladiyaStartResponse ->   //from room
                fillStartTaskData(baladiyaStartResponse)
            }, { error ->//
                showToastMessage(error.message.toString())
            })
    }

    private fun fillStartTaskData(baladiyaStartResponse: FieldWorkStartTaskResponse) {
        baladiyaStartResponse.data?.let { data ->
            data.followupWithBaladiyaCompleted?.let {
                when (it) {
                    resources.getStringArray(R.array.yes_no_drop_down)[0] -> {   // since 0 is by default and it's value is choose an option
                        binding.isFollowUpCompletedSpinner.setSelection(1)  //setting yes
                    }

                    resources.getStringArray(R.array.yes_no_drop_down)[1] -> {
                        binding.isFollowUpCompletedSpinner.setSelection(2)
                    }

                    else -> {
                        binding.isFollowUpCompletedSpinner.setSelection(0)
                    }
                }
            }
            data.baladiyaPermitAcquired?.let {
                when (it) {
                    resources.getStringArray(R.array.approved_rejected_dropdown)[0] -> {  //approved
                        binding.isTheBaladiyaPermitAcquiredSpinner.setSelection(1)  //setting approved
                    }

                    resources.getStringArray(R.array.approved_rejected_dropdown)[1] -> {
                        binding.isTheBaladiyaPermitAcquiredSpinner.setSelection(2)
                    }

                    else -> {
                        binding.isTheBaladiyaPermitAcquiredSpinner.setSelection(0)  // 0 mean choose an option
                    }
                }
            }

            binding.sadadBillerCodeValue.setText(data.sadadBillerCode?.toString())
            binding.accountNumberValue.setText(data.accountNumber)
            binding.paymentDaysValue.setText(data.paymentPeriodDays?.toString())

            data.documents?.let { doc ->
                for (document in doc) {
                    if (document!!.tagName?.isNotEmpty() == true) {
                        when (document.tagName) {
                            AppConstants.DocsTagNames.fieldWorkProvideEvidence -> {
                                provideEvidenceDoc.value = document
                            }

                            AppConstants.DocsTagNames.fieldWorkSadadDocument -> {
                                sadadDocument.value = document
                            }
                        }
                        showDocumentUploadFragUI()
                    }
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
}