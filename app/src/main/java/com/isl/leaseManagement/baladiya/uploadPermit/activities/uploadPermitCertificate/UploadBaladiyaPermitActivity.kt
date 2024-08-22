package com.isl.leaseManagement.baladiya.uploadPermit.activities.uploadPermitCertificate

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
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
import infozech.itower.databinding.ActivityUploadBaladiyaPermitCertificateBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class UploadBaladiyaPermitActivity : BaseActivity() {

    private lateinit var binding: ActivityUploadBaladiyaPermitCertificateBinding
    private var baladiyaCertificateDoc =
        MutableLiveData(SaveAdditionalDocument(taskId = MyApp.localTempVarStore.taskId))
    private lateinit var viewModel: UploadBaladiyaPermitViewModel

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_upload_baladiya_permit_certificate
        )
        init()
    }

    private fun init() {
        setTagNameToDocuments()
        setFragmentForUploadDoc(binding.uploadBaladiyaCertificate, baladiyaCertificateDoc)
        getStartTaskData()
        initViewModel()
        showDocumentUploadFragUI()
        setClickListeners()
    }

    private fun setTagNameToDocuments() {
        baladiyaCertificateDoc.value?.tagName =
            AppConstants.DocsTagNames.fieldWorkBaladiyaCertificate   // will use this to tag name on upload doc fragment to disable/enable document upload based on null or not,and will not save the doc if null in room or api
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

    private fun initViewModel() {
        val factory = UploadBaladiyaPermitViewModelFactory(UploadBaladiyaPermitRepository())
        viewModel =
            ViewModelProvider(this, factory)[UploadBaladiyaPermitViewModel::class.java]
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

                    val documents: MutableList<SaveAdditionalDocument?> =
                        mutableListOf()
                    baladiyaCertificateDoc.value?.let { doc ->
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
        fieldWorkResponse.additionalDocuments =
            null   // to avoid uploading it here as it cause error due to old doc

        fieldWorkResponse.data?.taskFlag = AppConstants.TaskFlags.taskBaladiyaUploadCertificate

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
                                if (docs[index]?.tagName == AppConstants.DocsTagNames.fieldWorkBaladiyaCertificate) {
                                    baladiyaCertificateDoc.value?.isDocumentUploadedToAPI = true
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
            binding.baladiyaRequestNumberTvValue.text = data.baladiyaRequestNumber ?: ""
            binding.trackingNumberTvValue.text = data.trackingNumber ?: ""
            binding.applicationSubmissionDateTvValue.text = Utilities.getDateFromISO8601(data.applicationSumissionDate ?: "")

            data.documents?.let { doc ->
                for (document in doc) {
                    if (document!!.tagName?.isNotEmpty() == true) {
                        when (document.tagName) {
                            AppConstants.DocsTagNames.fieldWorkBaladiyaCertificate -> {
                                baladiyaCertificateDoc.value = document
                            }
                        }
                        showDocumentUploadFragUI()
                    }
                }
            }
        }
    }

    private fun showDocumentUploadFragUI() {
        baladiyaCertificateDoc.value?.documentTypeName =
            AppConstants.DocTypeNames.baladiyaCertificate
        setFragmentForUploadDoc(binding.uploadBaladiyaCertificate, baladiyaCertificateDoc)
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

}