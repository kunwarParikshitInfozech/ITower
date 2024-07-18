package com.isl.leaseManagement.activities.basicDetails

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.isl.itower.MyApp
import com.isl.leaseManagement.activities.home.LsmHomeActivity
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClass.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.dataClass.requests.SubmitTaskRequest
import com.isl.leaseManagement.dataClass.requests.UploadDocumentRequest
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.Utilities
import com.isl.leaseManagement.utils.Utilities.showDatePickerFromCurrentDate
import com.isl.leaseManagement.utils.Utilities.toIsoString
import infozech.itower.R
import infozech.itower.databinding.ActivityBasicDetailsBinding
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Calendar

class BasicDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityBasicDetailsBinding
    private lateinit var viewModel: BasicDetailsViewModel
    private var paymentMethod: String? = null
    private var stringBase64: String? = null
    private var docId = ""
    private var processId = 0
    private var taskId = 0
    private var tagName = ""
    private val pickDocumentCode = 1;
    private val REQUEST_CODE_CAMERA = 2;

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
        binding.backIv.setOnClickListener { finish() }
        binding.submitBtn.setOnClickListener { submitData() }
        binding.sadadDocExpiryValue.setOnClickListener { showDatePickerAndFillDate(it as TextView) }
        binding.leaseRentExpiryValue.setOnClickListener { showDatePickerAndFillDate(it as TextView) }
        binding.attachSadadDocIv.setOnClickListener { openCameraDocPopup() }
        binding.attachLeaseRentVatIv.setOnClickListener { openCameraDocPopup() }
        fillStartDataDetailsAndApplyValidations()
    }

    private fun openCameraDocPopup() {
        if (docId == "") {
            val firstOptionText = "Camera" // Change this to your desired text
            val secondOptionText = "Document" // Change this to your desired text
            Utilities.showYesNoDialog(
                firstOptionName = firstOptionText,
                secondOptionName = secondOptionText,
                context = this, // Assuming you're calling from an activity, use 'this'
                title = "Choose Camera or file",
                message = "Select",
                firstOptionClicked = {
                    openCameraAndGetBase64String()
                },
                secondOptionClicked = {
                    pickDocumentAndGetBase64()
                }
            )
        } else {
            showToastMessage("Documents is already selected")
        }
    }

    private fun openCameraAndGetBase64String() {
        if (checkCameraPermission()) {
            openCamera()
        } else {
            requestCameraPermission()
        }

    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_CAMERA
        )
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickDocumentCode && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            processDocData(uri)
        }
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            val capturedImage = data?.extras?.get("data") as Bitmap
            val base64Image = getBase64StringFromBitmap(capturedImage)
            storeBase64AndShowDeleteUI(base64 = base64Image, name = "Camera", size = "Unknown")
        }
    }

    private fun getBase64StringFromBitmap(bitmap: Bitmap): String? {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, outputStream)
        val byteArray = outputStream.toByteArray()
        stringBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return stringBase64
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
        } else {
            showToastMessage("No Payment Method Available!")
        }
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
        //for doc
        var docList = ArrayList<SubmitTaskRequest.SubmitTaskData.Document>()
        val document = SubmitTaskRequest.SubmitTaskData.Document(docId)
        if (docId != "") {
            docList = arrayListOf()
            docList.add(document)
        } else {
            showToastMessage("Select Document to proceed!")
            return
        }

        //for additional doc
        val additionalDocList = ArrayList<SubmitTaskRequest.SubmitTaskData.Document>()
        val additionalDocIdList = KotlinPrefkeeper.additionalDocIdsForSubmitApi
        additionalDocIdList?.let {
            for (id in it) {
                additionalDocList.add(SubmitTaskRequest.SubmitTaskData.Document(id.toString()))
            }
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
        if (docId != "") {
            docList = arrayListOf()
            docList.add(document)
        } else {
            showToastMessage("Select Document to proceed!")
            return
        }

        //for additional doc
        val additionalDocList = ArrayList<SubmitTaskRequest.SubmitTaskData.Document>()
        val additionalDocIdList = KotlinPrefkeeper.additionalDocIdsForSubmitApi
        additionalDocIdList?.let {
            for (id in it) {
                additionalDocList.add(SubmitTaskRequest.SubmitTaskData.Document(id.toString()))
            }
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


    private fun pickDocumentAndGetBase64() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // Accepts various document types (adjust as needed)
        }
        startActivityForResult(intent, pickDocumentCode)
    }

    private fun processDocData(uri: Uri) {
        try {
            var fileName: String? = null
            var fileSize: Long = 0

            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    fileSize = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE)) ?: 0L

                }
            }

            // Check if file name and size are retrieved
            if (fileName != null && fileSize > 0) {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                var inputStream: InputStream? = null
                try {
                    inputStream = contentResolver.openInputStream(uri) ?: return
                    val bytes = inputStream.readBytes()
                    stringBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    storeBase64AndShowDeleteUI(
                        base64 = stringBase64,
                        name = fileName,
                        size = ((fileSize / 1024.0).toString() + " KB")
                    )
                } catch (e: Exception) {
                    // Handle exception
                } finally {
                    inputStream?.close()
                }
            } else {
                // Handle case where file name or size couldn't be retrieved
            }
        } catch (e: Exception) {
            showToastMessage("Error in parsing document!")
        }
    }


    private fun storeBase64AndShowDeleteUI(name: String?, size: String?, base64: String?) {
        if (base64 == null) {
            showToastMessage("Unable to get content string")
            return
        }
        val saveAdditionalDocument = SaveAdditionalDocument(
            docName = name, docSize = size, docContentString64 = base64
        )

        var deleteView = binding.deleteSadadDoc

        if (paymentMethod == AppConstants.KeyWords.iban) {
            deleteView = binding.deleteLeaseRentDoc    //checking which delete should work
        }

        deleteView.visibility = View.VISIBLE

        val docName: TextView = deleteView.findViewById(R.id.docName)
        val docSize: TextView = deleteView.findViewById(R.id.docSize)
        val docDelete: TextView = deleteView.findViewById(R.id.deleteDoc)

        saveAdditionalDocument.docName?.let {
            docName.text = getLastChars(it, 16)
        }
        size?.let {
            docSize.text = getLastChars(it, 10)
        }
        uploadDocument()
        docDelete.setOnClickListener {
            deleteView.visibility = View.GONE
            docId = ""
        }
    }

    private fun getLastChars(str: String, maxLength: Int): String {
        val length = str.length
        return if (length <= maxLength) str else str.substring(length - maxLength)
    }

    private fun uploadDocument() {
        val taskId = MyApp.localTempVarStore.taskId
        //     val taskId = 3263  // for testing
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
                //       tagName = AppConstants.KeyWords.leaseRentDocTagName,    //for testing
                timeStamp = "",
                userId = 123
            )

        showProgressBar()
        viewModel.uploadDocument(
            { successResponse ->
                successResponse?.let { response ->
                    hideProgressBar()
                    response.docId?.let {
                        docId = it
                        showToastMessage("Document Uploaded Successfully!")
                    }
                }
            },
            { errorMessage ->
                hideProgressBar()
                showToastMessage("Unable to upload Document!")
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