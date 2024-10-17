package com.isl.leaseManagement.paymentProcess.activities.basicDetails

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.isl.itower.MyApp
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.common.activities.home.LsmHomeActivity
import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.dataClasses.requests.SubmitTaskRequest
import com.isl.leaseManagement.dataClasses.requests.SubmitTaskRequest.SubmitTaskData
import com.isl.leaseManagement.dataClasses.requests.UploadDocumentRequest
import com.isl.leaseManagement.dataClasses.responses.PaymentStartTaskResponse
import com.isl.leaseManagement.room.entity.common.SaveAdditionalDocumentPOJO
import com.isl.leaseManagement.room.entity.paymentProcess.StartTaskPaymentPOJO
import com.isl.leaseManagement.room.entity.paymentProcess.SubmitTaskRequestPOJO
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities
import com.isl.leaseManagement.utils.Utilities.showDatePickerAndFillDate
import com.isl.leaseManagement.utils.Utilities.toIsoString
import infozech.itower.R
import infozech.itower.databinding.ActivityBasicDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

class BasicDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityBasicDetailsBinding
    private lateinit var viewModel: BasicDetailsViewModel
    private var paymentMethod: String? = null
    private var stringBase64: String? = null
    private var tagName = ""
    private val pickDocumentCode = 1;
    private val requestCameraCode = 2;
    private var saveAdditionalDocumentList = ArrayList<SaveAdditionalDocument>()
    private var currentTaskId = 0
    private var submitTaskCompleteDocumentForRoom: SaveAdditionalDocument? = null
    private var submitTaskRequest = SubmitTaskRequest(SubmitTaskData(), 0)  //created empty
    private var processId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_basic_details)
        init()
    }

    private fun init() {
        val factory = BasicDetailsViewModelFactory(BasicDetailsRepository())
        viewModel = ViewModelProvider(this, factory)[BasicDetailsViewModel::class.java]
        currentTaskId = MyApp.localTempVarStore.taskId
        setClickListeners()
        fillDataOfStartApiAndApplyValidations()
        getAdditionalDocListOfThisTask()   //calling it at start to give enough time
    }

    private var docFromStartTask: PaymentStartTaskResponse.StartTaskData.StartTaskDocument? = null
    private var shouldWeUpdateSubmitDocFromStart = false

    private fun fillDataOfStartApiAndApplyValidations() {
        MyApp.localTempVarStore?.let { tempVarStorage ->
            tempVarStorage.paymentStartTaskResponse?.let { response ->
                response.data?.let { responseData ->
                    responseData.sadadBillerCode?.let { binding.billerCodeEt.setText(it) }
                    responseData.accountNumber?.let { binding.accountNumberEt.setText(it) }
                    responseData.sadadBillerCode?.let { binding.billerCodeEt.setText(it) }
                    responseData.sadadExpiryDate?.let { date ->
                        binding.sadadDocExpiryValue.text = Utilities.getDateFromISO8601(date)
                    }
                    responseData.rentVATExpiryDate?.let { date ->
                        binding.leaseRentExpiryValue.text = Utilities.getDateFromISO8601(date)
                    }
                    when (responseData.paymentMethod) {
                        AppConstants.PaymentType.paymentTypeCheck -> {
                            paymentMethod = AppConstants.PaymentType.paymentTypeCheck
                            //          binding.billerCodeEt.isEnabled = false
                            binding.sadadBillerCodeTv.visibility = View.GONE
                            binding.billerCodeEt.visibility = View.GONE
                            binding.accountNumberEt.isEnabled = false
                            binding.sadadDocExpiryTv.visibility = View.GONE
                            binding.sadadDocExpiryValue.visibility = View.GONE
                            binding.leaseRentVatExpiryDateTv.visibility = View.GONE
                            binding.leaseRentExpiryValue.visibility = View.GONE
                            binding.sadadOrLeaseDocumentTv.visibility = View.GONE
                            binding.attachDocumentIv.visibility = View.GONE
                            binding.clBasicDetails.visibility = View.GONE
                        }

                        AppConstants.PaymentType.paymentTypeSadad -> {
                            paymentMethod = AppConstants.PaymentType.paymentTypeSadad
                            binding.leaseRentVatExpiryDateTv.visibility = View.GONE
                            binding.leaseRentExpiryValue.visibility = View.GONE
                            binding.sadadOrLeaseDocumentTv.text = getString(R.string.sadad_document)
                            tagName = AppConstants.DocsTagNames.sadadDocumentTagName
                        }

                        AppConstants.PaymentType.paymentTypeIban -> {
                            paymentMethod = AppConstants.PaymentType.paymentTypeIban
                            //               binding.billerCodeEt.isEnabled = false
                            binding.sadadBillerCodeTv.visibility = View.GONE
                            binding.billerCodeEt.visibility = View.GONE
                            binding.sadadDocExpiryTv.visibility = View.GONE
                            binding.sadadDocExpiryValue.visibility = View.GONE
                            binding.sadadOrLeaseDocumentTv.text =
                                getString(R.string.lease_rent_vat_document)
                            tagName = AppConstants.DocsTagNames.leaseRentDocTagName
                        }

                        else -> { // when case ends
                            showToastMessage("Payment type not found!")
                            return
                        }
                    }
                    if (responseData.shouldUpdateSubmitDocFromStart == 1) {
                        shouldWeUpdateSubmitDocFromStart = true
                    }
                    responseData.documents?.get(0)?.let { // doc is received from start task,
                        docFromStartTask = it
                    }
                }
                response.processId?.let {
                    submitTaskRequest.processId = it
                    processId = it
                }
            }
            currentTaskId = tempVarStorage.taskId
        }
        if (docFromStartTask != null && shouldWeUpdateSubmitDocFromStart) {
            uploadStartTaskDocGetDocID(docFromStartTask!!)
        } else {
            fetchSubmitDataFromRoomAndFill()     //if doc is not present, simply loading from room , but if present uploading it and saving in room than fetching it from their
        }
    }

    private fun uploadStartTaskDocGetDocID(docFromStartTask: PaymentStartTaskResponse.StartTaskData.StartTaskDocument) {

        if (docFromStartTask.content == null || docFromStartTask.content!!.isEmpty() || tagName.isEmpty() || KotlinPrefkeeper.lsmUserId == null || KotlinPrefkeeper.lsmUserId!!.isEmpty()
            || processId == 0
        ) {
            fetchSubmitDataFromRoomAndFill()
            return
        }

        val uploadDocumentRequest =
            UploadDocumentRequest(
                content = docFromStartTask.content,
                fileName = docFromStartTask.fileName,

                requestId = MyApp.localTempVarStore.taskResponse?.requestId,
                tagName = tagName,
                timeStamp = "",
                userId = KotlinPrefkeeper.lsmUserId!!.toInt()
            )

        showProgressBar()
        viewModel.uploadDocument(
            { successResponse ->
                successResponse?.let { response ->
                    hideProgressBar()
                    if (response.docId != null) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                submitTaskCompleteDocumentForRoom = SaveAdditionalDocument(
                                    taskId = currentTaskId
                                )
                                submitTaskCompleteDocumentForRoom?.fileName =
                                    docFromStartTask.fileName
                                submitTaskCompleteDocumentForRoom?.content =
                                    docFromStartTask.content
                                submitTaskCompleteDocumentForRoom?.docId = response.docId
                                withContext(Dispatchers.Main) {
                                    saveSubmitDetails()
                                }
                                delay(100) // for allowing saving of data
                                fetchSubmitDataFromRoomAndFill()
                                val starTaskDao = MyApp.getMyDatabase()
                                    .startTaskDao()  //start data saved to submit doc, now removing it from start to avoid overriding it
                                starTaskDao?.getStartTaskById(currentTaskId)?.let { startTaskPOJO ->
                                    val startDataClass =
                                        convertStartTaskPOJOtoDataClass(startTaskPOJO)
                                    startDataClass.data?.documents?.get(0)?.content = ""
                                    startDataClass.data?.documents?.get(0)?.fileName = ""
                                    startDataClass.data?.shouldUpdateSubmitDocFromStart =
                                        2 // now, it wouldn't update
                                    MyApp.localTempVarStore.paymentStartTaskResponse?.data?.shouldUpdateSubmitDocFromStart =
                                        2// for this session that is no need to get updated value from start task
                                    val startTaskPojo =
                                        convertToStartTaskResponsePOJO(startDataClass)
                                    starTaskDao.insertStartTask(startTaskPojo)
                                }

                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    showToastMessage("Exception in saving submit doc!")
                                }
                                fetchSubmitDataFromRoomAndFill()
                            }
                        }
                    } else {
                        fetchSubmitDataFromRoomAndFill()
                    }
                }
            },
            { errorMessage ->
                hideProgressBar()
                fetchSubmitDataFromRoomAndFill()
            }, taskId = currentTaskId,
            body =
            uploadDocumentRequest
        )
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


    private fun fetchSubmitDataFromRoomAndFill() {
        lifecycleScope.launch(Dispatchers.IO) {
            val submitTaskPOJO =
                MyApp.getMyDatabase().submitTaskDao().getSubmitTaskById(currentTaskId)
            //    fillSubmitDataOfRoomFromMain(submitTaskPOJO)
            delay(100)
            withContext(Dispatchers.Main) {
                submitTaskPOJO?.data?.let { data ->
                    data.sadadBillerCode?.let {
                        if (it != 0) {
                            binding.billerCodeEt.setText(it.toString())
                        }
                    } // filling all 4 fields
                    data.accountNumber?.let { binding.accountNumberEt.setText(it) }
                    binding.sadadDocExpiryValue.text = data.sadadExpiryDate ?: ""
                    binding.leaseRentExpiryValue.text = data.rentVATExpiryDate ?: ""
                    if (data.document != null && data.document.fileName != null) {
                        if (paymentMethod != AppConstants.PaymentType.paymentTypeCheck) { //filling delete layout
                            binding.docName.text = getLastChars(data.document!!.fileName!!, 14)
                            if (data.document.docSize != null) {
                                binding.docSize.text = getLastChars(data.document.docSize!!, 12)
                            }
                            binding.rlDeleteDocLayout.visibility = View.VISIBLE
                            data.document.docId?.let {
                                submitTaskRequest.data.documents =
                                    listOf(SubmitTaskData.Document(it))   //filled document ID in submit task request
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.submitBtn.setOnClickListener { submitDataToAPI() }
        binding.saveBtn.setOnClickListener {
            showToastMessage("Submit Data Saved!")
            saveSubmitDetails()
        }
        binding.sadadDocExpiryValue.setOnClickListener {
            showDatePickerAndFillDate(
                it as TextView,
                this
            )
        }
        binding.leaseRentExpiryValue.setOnClickListener {
            showDatePickerAndFillDate(
                it as TextView,
                this
            )
        }
        binding.attachDocumentIv.setOnClickListener { openCameraDocPopup() }
        binding.deleteDocBtn.setOnClickListener { deleteDocumentClicked() }
    }

    private fun openCameraDocPopup() {
        if (binding.rlDeleteDocLayout.visibility == View.GONE) {
            val firstOptionText = "Camera" // Change this to your desired text
            val secondOptionText = "Document" // Change this to your desired text
            Utilities.showYesNoDialog(
                context = this,
                message = "Select Camera OR Document",
                firstOptionName = firstOptionText,
                secondOptionName = secondOptionText,
                optionSelection = object : ClickInterfaces.TwoOptionSelection {
                    override fun option1Selected() {
                        openCameraAndGetBase64String()
                    }

                    override fun option2Selected() {
                        selectDocAndGetBase64()
                    }

                }
            )
        } else {
            showToastMessage("Document is already selected")
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
            requestCameraCode
        )
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, requestCameraCode)
    }

    private fun selectDocAndGetBase64() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // Set to a generic type that supports multiple file types
            putExtra(
                Intent.EXTRA_MIME_TYPES, arrayOf(
                    "image/jpeg",
                    "image/png",
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "text/csv",
                    "image/svg+xml"
                )
            )
        }
        startActivityForResult(intent, pickDocumentCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickDocumentCode && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            processDocumentAndCallUpload(uri)
        }
        if (requestCode == requestCameraCode && resultCode == RESULT_OK) {
            val capturedImage = data?.extras?.get("data") as Bitmap
            val result = getBase64StringAndSizeFromBitmapForCamera(capturedImage)
            val (base64String, imageSize) = result
            callUploadDocumentAndUpdateAll3Places(
                SaveAdditionalDocument(
                    taskId = MyApp.localTempVarStore.taskId,
                    content = base64String,
                    docSize = "$imageSize KB"
                )
            )
        }
    }

    private fun getBase64StringAndSizeFromBitmapForCamera(bitmap: Bitmap): Pair<String?, Int> {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        val stringBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        val imageSize =
            outputStream.size().toLong() // Get size from output stream after compression

        return Pair(stringBase64, ((imageSize).toInt()))
    }


    @SuppressLint("Range")
    private fun processDocumentAndCallUpload(uri: Uri) {
        try {
            var fileName: String? = null
            var fileSize: Long = 0

            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    fileSize = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
                    if (fileSize > 10 * 1024 * 1024) { // 10 MB in bytes
                        showToastMessage("File size exceeds 10 MB. Please choose a smaller file.")
                        return
                    }
                }
            }
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
                    val saveAdditionalDocument = SaveAdditionalDocument(
                        taskId = MyApp.localTempVarStore.taskId,
                        content = stringBase64,
                        fileName = fileName,
                        docSize = (((fileSize / 1024.0).toInt()).toString() + " KB"),
                        docId = ""  //id not available yet, need to upload the doc to API
                    )
                    callUploadDocumentAndUpdateAll3Places(saveAdditionalDocument)
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

    private fun callUploadDocumentAndUpdateAll3Places(saveAdditionalDocument: SaveAdditionalDocument) {
        if (saveAdditionalDocument.content == null) {
            showToastMessage("Please select document before uploading!")
            return
        }

        if (tagName.isEmpty()) {
            showToastMessage("Unable to upload document Tag Name is Empty!")
            return
        }

        val uploadDocumentRequest =
            UploadDocumentRequest(
                content = saveAdditionalDocument.content,
                fileName = saveAdditionalDocument.fileName,
                requestId = MyApp.localTempVarStore.taskResponse?.requestId,
                tagName = tagName,
                timeStamp = "",
                userId = 123
            )

        showProgressBar()
        viewModel.uploadDocument(
            { successResponse ->
                successResponse?.let { response ->
                    hideProgressBar()
                    if (response.docId != null) {
                        saveAdditionalDocument.docId =
                            response.docId
                        submitTaskRequest.data.documents = //first, updating submit request for API
                            listOf(SubmitTaskData.Document(saveAdditionalDocument.docId))

                        if (saveAdditionalDocument.fileName != null) {  //second, updating delete layout and showing
                            binding.docName.text =
                                getLastChars(saveAdditionalDocument.fileName!!, 14)
                        } else {
                            binding.docName.text = "Unknown"
                        }

                        if (saveAdditionalDocument.docSize != null) {
                            binding.docSize.text =
                                getLastChars(saveAdditionalDocument.docSize!!, 12)
                        } else {
                            binding.docSize.text = "Unknown"
                        }

                        binding.rlDeleteDocLayout.visibility = View.VISIBLE

                        submitTaskCompleteDocumentForRoom =
                            saveAdditionalDocument  // third, saving complete doc for saving to room
                    } else {
                        showToastMessage("Document ID received empty!")
                    }
                }
            },
            { errorMessage ->
                hideProgressBar()
                showToastMessage("Unable to upload Document!")
            }, taskId = currentTaskId,
            body =
            uploadDocumentRequest
        )
    }

    private fun deleteDocumentClicked() {
        submitTaskRequest.data.documents = listOf()   //first,  made it empty
        binding.rlDeleteDocLayout.visibility = View.GONE  //second, delete visibility gone
        submitTaskCompleteDocumentForRoom = null  //third
    }

    private fun submitDataToAPI() {   //note -  submitTaskRequest already have process id and document
        if (paymentMethod != null) {
            when (paymentMethod) {
                AppConstants.PaymentType.paymentTypeCheck -> {
                    submitInCheckCase()
                }

                AppConstants.PaymentType.paymentTypeSadad -> {
                    //for additional doc
                    val additionalDocList = ArrayList<SubmitTaskData.Document>()
                    saveAdditionalDocumentList.let {
                        for (completeDoc in it) {
                            additionalDocList.add(SubmitTaskData.Document(completeDoc.docId))
                        }
                    }
                    submitInSADADCase(additionalDocList)
                }

                AppConstants.PaymentType.paymentTypeIban -> {
                    //for additional doc
                    val additionalDocList = ArrayList<SubmitTaskData.Document>()
                    saveAdditionalDocumentList.let {
                        for (id in it) {
                            additionalDocList.add(SubmitTaskData.Document(id.docId.toString()))
                        }
                    }
                    submitInIbanCase(additionalDocList)

                }
            }
        } else {
            showToastMessage("No Payment Method Available!")
        }
    }

    private fun getAdditionalDocListOfThisTask() {
        lifecycleScope.launch(Dispatchers.IO) {
            val documentsList =
                MyApp.getMyDatabase().saveAdditionalDocumentDao()
                    .getAllSavedDocumentsOfATask(currentTaskId.toString()) as ArrayList<SaveAdditionalDocumentPOJO>
            saveAdditionalDocumentList = convertPOJOListToDocumentList(documentsList)
        }
    }

    private fun convertPOJOListToDocumentList(pojoList: ArrayList<SaveAdditionalDocumentPOJO>): ArrayList<SaveAdditionalDocument> {
        val documentList = ArrayList<SaveAdditionalDocument>()
        for (pojo in pojoList) {
            val document = SaveAdditionalDocument(
                taskId = pojo.taskId ?: 0,
                fileName = pojo.docName,
                docSize = pojo.docSize,
                content = pojo.docContentString64,
                docId = pojo.docUploadId
            )
            documentList.add(document)
        }
        return documentList
    }

    private fun submitInCheckCase() {
        val docList = ArrayList<SubmitTaskData.Document>()
        val additionalDocList = ArrayList<SubmitTaskData.Document>()

        //have to pass empty in check case
        submitTaskRequest.data = SubmitTaskData(
            additionalDocuments = additionalDocList,  //will be empty
            documents = docList,
            paymentMethod = paymentMethod,
        )
        callSubmitDataApi(currentTaskId, submitTaskRequest)
    }

    private fun submitInSADADCase(additionalDocList: ArrayList<SubmitTaskData.Document>) {
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
            return
        }

        if (submitTaskRequest
                .data.documents == null || submitTaskRequest.data.documents!!.isEmpty()
        ) {
            showToastMessage("Select Document")
            return
        }

        submitTaskRequest.data =
            SubmitTaskData(    //document is already updated by room and by selection
                sadadBillerCode = sadadBillerCode.toInt(),
                accountNumber = accountNumber,
                documents = submitTaskRequest.data.documents,
                additionalDocuments = additionalDocList,
                paymentMethod = paymentMethod,
                sadadExpiryDate = sadadExpiryDate
            )
        callSubmitDataApi(currentTaskId, submitTaskRequest)
    }

    private fun submitInIbanCase(additionalDocList: ArrayList<SubmitTaskData.Document>) {
        if (!Utilities.IBANValidityCheck.checkIfIbanNumberIsValid(binding.accountNumberEt.text.toString())) {
            showToastMessage("Enter Valid IBAN number!")
            return
        }

        val accountNumber = binding.accountNumberEt.text.toString()

        if (binding.leaseRentExpiryValue.text == null || binding.leaseRentExpiryValue.text.toString()
                .isEmpty()
        ) {
            showToastMessage("Select Lease Rent Expiry date!")
            return
        }
        val leaseExpiryDate = toIsoString(binding.leaseRentExpiryValue.text.toString())
        if (leaseExpiryDate == null) {
            showToastMessage("Select Lease Expiry Date")
            return
        }
        if (submitTaskRequest
                .data.documents == null || submitTaskRequest.data.documents!!.isEmpty()
        ) {
            showToastMessage("Select Document")
            return
        }

        submitTaskRequest.data = SubmitTaskData(
            accountNumber = accountNumber,
            documents = submitTaskRequest.data.documents,
            additionalDocuments = additionalDocList,
            paymentMethod = paymentMethod,
            rentVATExpiryDate = leaseExpiryDate
        )
        callSubmitDataApi(currentTaskId, submitTaskRequest)
    }

    private fun callSubmitDataApi(currentTaskId: Int, submitTaskRequest: SubmitTaskRequest) {
        showProgressBar()
        viewModel.submitTask(
            { successResponse ->
                successResponse?.let { it ->
                    hideProgressBar()
                    it.flag?.let { flagNN ->
                        if (flagNN == "0") {
                            showToastMessage("Task Submitted Successfully!")
                            lifecycleScope.launch(Dispatchers.IO) {

                                MyApp.getMyDatabase().saveAdditionalDocumentDao()
                                    .deleteAllDocumentsOfTask(currentTaskId)  //deleting additional docs

                                MyApp.getMyDatabase().startTaskDao()    //deleting start task  data
                                    .deleteStartTaskByTaskId(currentTaskId)

                                MyApp.getMyDatabase().submitTaskDao()    //deleting submit data
                                    .deleteSubmitTaskByTaskId(currentTaskId)

                                MyApp.getMyDatabase()
                                    .taskResponseDao()    //deleting task response  data
                                    .deleteTaskResponseByTaskId(currentTaskId)

                                delay(100)
                                launchNewActivityCloseAllOther(LsmHomeActivity::class.java)

                            }
                        }
                    }
                }
            },
            { errorMessage ->
                hideProgressBar()
                showToastMessage("Unable to submit!")
                binding.progressBar.visibility = View.GONE
            }, taskId = currentTaskId,
            body =
            submitTaskRequest
        )
    }

    private fun saveSubmitDetails() {
        var sadadBillerInt = 0
        if (binding.billerCodeEt.text.toString() != "") {
            sadadBillerInt = binding.billerCodeEt.text.toString().toInt()
        }
        val accountNumber = binding.accountNumberEt.text.toString()
        val sadadExpiryDate = binding.sadadDocExpiryValue.text.toString()
        val rentVATExpiryDate = binding.leaseRentExpiryValue.text.toString()

        val paymentMethod = paymentMethod

        val submitTaskData = SubmitTaskRequestPOJO.SubmitTaskData(
            accountNumber,
            submitTaskCompleteDocumentForRoom,
            paymentMethod,
            rentVATExpiryDate,
            sadadBillerInt,
            sadadExpiryDate
        )

        val submitTaskRequestPOJO =
            SubmitTaskRequestPOJO(
                currentTaskId,
                submitTaskData,
                null
            ) // processId can be null

        lifecycleScope.launch(Dispatchers.IO) {
            MyApp.getMyDatabase().submitTaskDao().insertSubmitTask(submitTaskRequestPOJO)
        }

    }


    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun getLastChars(str: String, maxLength: Int): String {
        val length = str.length
        return if (length <= maxLength) str else str.substring(length - maxLength)
    }
}