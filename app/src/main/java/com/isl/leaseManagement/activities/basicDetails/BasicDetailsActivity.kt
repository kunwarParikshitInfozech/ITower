package com.isl.leaseManagement.activities.basicDetails

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.isl.itower.MyApp
import com.isl.leaseManagement.activities.home.LsmHomeActivity
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClass.requests.SubmitTaskRequest
import com.isl.leaseManagement.dataClass.requests.UploadDocumentRequest
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.Utilities
import com.isl.leaseManagement.utils.Utilities.showDatePickerFromCurrentDate
import com.isl.leaseManagement.utils.Utilities.toIsoString
import infozech.itower.R
import infozech.itower.databinding.ActivityBasicDetailsBinding
import java.io.InputStream
import java.util.Calendar

class BasicDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityBasicDetailsBinding
    private lateinit var viewModel: BasicDetailsViewModel
    private var paymentMethod: String? = null
    private var stringBase64: String? = null
    private val pickDocumentCode = 1;
    private var docId = ""
    private var processId = 0
    private var taskId = 0
    private var tagName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_basic_details)
        init()
    }

    private fun init() {
        val factory = BasicDetailsViewModelFactory(BasicDetailsRepository())
        viewModel = ViewModelProvider(this, factory).get(BasicDetailsViewModel::class.java)
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { launchNewActivityCloseAllOther(LsmHomeActivity::class.java) }
        binding.submitBtn.setOnClickListener { submitData() }
        binding.sadadDocExpiryValue.setOnClickListener { showDatePickerAndFillDate(it as TextView) }
        binding.leaseRentExpiryValue.setOnClickListener { showDatePickerAndFillDate(it as TextView) }
        binding.attachSadadDocIv.setOnClickListener { pickDocument() }
        binding.attachLeaseRentVatIv.setOnClickListener { pickDocument() }
        fillStartDataDetailsAndApplyValidations()
    }

    private fun showDatePickerAndFillDate(view: TextView) {
        showDatePickerFromCurrentDate(this@BasicDetailsActivity) { selectedDate ->
            val year = selectedDate.get(Calendar.YEAR)
            val month = selectedDate.get(Calendar.MONTH) + 1 // Months are 0-based
            val day = selectedDate.get(Calendar.DAY_OF_MONTH)

            view.text = "$day/$month/$year"
        }
    }

    private fun fillStartDataDetailsAndApplyValidations() {
        MyApp.localTempVarStore?.let { tempVarStorage ->
            tempVarStorage.startTaskResponse?.let { response ->
                response.data?.let { responseData ->
                    responseData.sadadBillerCode?.let { binding.billerCodeEt.setText(it) }
                    responseData.accountNumber?.let { binding.accountNumberEt.setText(it) }
                    responseData.sadadBillerCode?.let { binding.billerCodeEt.setText(it) }
                    when (responseData.paymentMethod) {
                        AppConstants.KeyWords.check -> {
                            paymentMethod = AppConstants.KeyWords.check
                            binding.billerCodeEt.isEnabled = false
                            binding.accountNumberEt.isEnabled = false
                            binding.sadadDocExpiryValue.setOnClickListener(null)
                            binding.leaseRentExpiryValue.setOnClickListener(null)
                            binding.attachSadadDocIv.setOnClickListener(null)
                            binding.attachLeaseRentVatIv.setOnClickListener(null)
                        }

                        AppConstants.KeyWords.sadad -> {
                            paymentMethod = AppConstants.KeyWords.sadad
                            binding.leaseRentExpiryValue.setOnClickListener(null)
                            binding.attachLeaseRentVatIv.setOnClickListener(null)
                            tagName = AppConstants.KeyWords.sadadDocumentTagName
                        }

                        AppConstants.KeyWords.iban -> {
                            paymentMethod = AppConstants.KeyWords.iban
                            binding.billerCodeEt.isEnabled = false
                            binding.sadadDocExpiryValue.setOnClickListener(null)
                            binding.attachSadadDocIv.setOnClickListener(null)
                            tagName = AppConstants.KeyWords.leaseRentDocTagName
                        }

                        else -> {// do nothing
                        }
                    }
                }
                response.processId?.let { processId = it }
            }
            taskId = tempVarStorage.taskId
        }
    }

    private fun submitData() {
        if (paymentMethod != null) {
            when (paymentMethod) {
                AppConstants.KeyWords.check -> {
                    submitInCheckCase()
                }

                AppConstants.KeyWords.sadad -> {
                    submitInSADADCase()
                }

                AppConstants.KeyWords.iban -> {
                    submitInIbanCase()

                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        launchNewActivityCloseAllOther(LsmHomeActivity::class.java)
    }

    private fun submitInCheckCase() {
        val docList = ArrayList<SubmitTaskRequest.SubmitTaskData.Document>()
        val additionalDocList = ArrayList<SubmitTaskRequest.SubmitTaskData.Document>()

        val submitTaskData = SubmitTaskRequest.SubmitTaskData(
            additionalDocuments = additionalDocList,
            documents = docList,
            paymentMethod = paymentMethod,
        )
        val submitTaskRequest = SubmitTaskRequest(processId = processId, data = submitTaskData)
        callSubmitDataApi(taskId, submitTaskRequest)
    }

    private fun submitInSADADCase() {
        val accountNumber = binding.accountNumberEt.text.toString()
        if (accountNumber.isEmpty()) {
            showToastMessage("Enter account number")
            return
        }
        val sadadBillerCode = binding.billerCodeEt.text.toString()
        if (sadadBillerCode.isEmpty() || sadadBillerCode.length != 3) {
            showToastMessage("Enter valid sadad biller code")
            return
        }
        if (binding.sadadDocExpiryValue.text == null || binding.sadadDocExpiryValue.text.toString()
                .isEmpty()
        ) {
            showToastMessage("Select SADAD Expiry date!")
            return
        }
        val sadadExpiryDate = toIsoString(binding.sadadDocExpiryValue.text.toString())
        if (sadadExpiryDate == null) {
            showToastMessage("Select SADAD Expiry Date")
        }
        var docList = ArrayList<SubmitTaskRequest.SubmitTaskData.Document>()
        var additionalDocList = ArrayList<SubmitTaskRequest.SubmitTaskData.Document>()

        val document = SubmitTaskRequest.SubmitTaskData.Document(docId)

        if (docId != "") {
            docList = arrayListOf()
            docList.add(document)
        }

        val submitTaskData = SubmitTaskRequest.SubmitTaskData(
            sadadBillerCode = sadadBillerCode.toInt(),
            accountNumber = accountNumber,
            additionalDocuments = additionalDocList,
            documents = docList,
            paymentMethod = paymentMethod,
            sadadExpiryDate = sadadExpiryDate
        )
        val submitTaskRequest = SubmitTaskRequest(processId = processId, data = submitTaskData)
        callSubmitDataApi(taskId, submitTaskRequest)
    }

    private fun submitInIbanCase() {
        if (!Utilities.IBANValidityCheck.checkIfIbanNumberIsValid(binding.accountNumberEt.text.toString())) {
            showToastMessage("Enter Valid IBAN number!")
            return
        }

        val accountNumber = binding.accountNumberEt.text.toString()
        val document = SubmitTaskRequest.SubmitTaskData.Document(docId)
        var docList = ArrayList<SubmitTaskRequest.SubmitTaskData.Document>()
        var additionalDocList = ArrayList<SubmitTaskRequest.SubmitTaskData.Document>()
        if (docId != "") {
            docList = arrayListOf()
            docList.add(document)
        }
        if (binding.leaseRentExpiryValue.text == null || binding.leaseRentExpiryValue.text.toString()
                .isEmpty()
        ) {
            showToastMessage("Select Lease Rent Expiry date!")
            return
        }
        val leaseExpiryDate = toIsoString(binding.leaseRentExpiryValue.text.toString())
        if (leaseExpiryDate == null) {
            showToastMessage("Select Lease Expiry Date")
        }
        val submitTaskData = SubmitTaskRequest.SubmitTaskData(
            accountNumber = accountNumber,
            additionalDocuments = additionalDocList,
            documents = docList,
            paymentMethod = paymentMethod,
            rentVATExpiryDate = leaseExpiryDate
        )
        val submitTaskRequest = SubmitTaskRequest(processId = processId, data = submitTaskData)
        callSubmitDataApi(taskId, submitTaskRequest)
    }


    private fun pickDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // Accepts various document types (adjust as needed)
        }
        startActivityForResult(intent, pickDocumentCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickDocumentCode && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            // Request temporary read access using takePersistableUriPermission with the appropriate flag
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            ) // This is the correct flag
            var inputStream: InputStream? = null
            try {
                inputStream = contentResolver.openInputStream(uri) ?: return
                val bytes = inputStream.readBytes()
                stringBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                uploadDocument()
            } catch (e: Exception) {       //   exception case
            } finally {
                inputStream?.close()
            }
        }
    }

    private fun uploadDocument() {
        val taskId = MyApp.localTempVarStore.taskId
        if (stringBase64 == null) {
            showToastMessage("Please select document before uploading!")
            return
        }
        val uploadDocumentRequest =
            UploadDocumentRequest(
                content = stringBase64,
                fileName = "",
                latitude = 0,
                longitude = 0,
                requestId = MyApp.localTempVarStore.taskResponse?.requestId,
                tagName = tagName,
                timeStamp = "",
                userId = 123
            )

        showProgressBar()
        viewModel.uploadDocument(
            { successResponse ->
                successResponse?.let {
                    hideProgressBar()
                    it.docId?.let {
                        docId = it
                        showToastMessage("Document Uploaded Successfully!")
                    }
                }
            },
            { errorMessage ->
                hideProgressBar()
            }, taskId = taskId,
            body =
            uploadDocumentRequest
        )
    }

    private fun callSubmitDataApi(taskId: Int, submitTaskRequest: SubmitTaskRequest) {
        showProgressBar()
        viewModel.submitTask(
            { successResponse ->
                successResponse?.let { it ->
                    hideProgressBar()
                    it.flag?.let { flagNN ->
                        if (flagNN == "0") {
                            showToastMessage("Task Submitted Successfully!")
                            launchNewActivityCloseAllOther(LsmHomeActivity::class.java)
                        }
                    }
                }
            },
            { errorMessage ->
                hideProgressBar()
                binding.progressBar.visibility = View.GONE
            }, taskId = taskId,
            body =
            submitTaskRequest
        )
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
}