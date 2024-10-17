package com.isl.leaseManagement.common.fragments.uploadDoc

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.isl.itower.MyApp
import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.base.BaseFragment
import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.dataClasses.requests.DeleteDocumentRequest
import com.isl.leaseManagement.dataClasses.requests.UploadDocumentRequest
import com.isl.leaseManagement.dataClasses.responses.ApiSuccessFlagResponse
import com.isl.leaseManagement.dataClasses.responses.UploadDocumentResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities
import infozech.itower.R
import infozech.itower.databinding.FragmentUploadSingleDocumentBinding
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.UUID

class UploadSingleDocumentFragment(
    private var saveAdditionalDocument: MutableLiveData<SaveAdditionalDocument>,
    private val docDeletedUpdateToPhone: ClickInterfaces.CommonInterface
) :
    BaseFragment() {

    private lateinit var binding: FragmentUploadSingleDocumentBinding
    private val pickDocumentCode = 1;
    private val requestCameraCode = 2;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadSingleDocumentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        fillUiFromReceivedDoc()
    }

    private fun fillUiFromReceivedDoc() {
        binding.docTypeTv.text = saveAdditionalDocument.value?.documentTypeName
            ?: ""   //common, since we wanna show doc type name even if field is disabled
        if (saveAdditionalDocument.value?.tagName != null) {    // tag name used for enabled/ disabled as well along with uploading to API
            documentIsEditable()
        } else {
            documentIsUneditable()
        }
    }

    private fun documentIsEditable() {
        binding.attachDocumentIv.setOnClickListener { openCameraDocPopup() }
        //   if (saveAdditionalDocument.value?.docContentString64 != null && saveAdditionalDocument.value?.docContentString64 != "") {
        binding.docName.text =
            saveAdditionalDocument.value?.fileName ?: getString(R.string.no_document_selected)
        binding.docSize.text = saveAdditionalDocument.value?.docSize ?: getString(R.string.size)
        //  }
        binding.downloadIv.setOnClickListener { downloadDocument() }
        binding.deleteIcon.setOnClickListener { deleteDocumentFromApi() }
    }

    private fun deleteDocumentFromApi() {
        val docId = saveAdditionalDocument.value?.docId?.toIntOrNull()
        if (docId == null) {
            baseActivity.showToastMessage("Unable to delete task ID is empty!")
            return
        }
        val requestId = MyApp.localTempVarStore?.taskResponse?.requestId
        requestId ?: return

        val tagName = saveAdditionalDocument.value?.tagName
        tagName ?: return

        val taskId = MyApp.localTempVarStore?.taskId
        taskId ?: return

        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""

        val isDocIdPermanent = saveAdditionalDocument.value?.isDocIdPermanent
        isDocIdPermanent ?: return

        val api = ApiClient.request
        val observable: Observable<ApiSuccessFlagResponse> =
            api!!.deleteDocument(
                userId = lsmUserId,
                documentID = docId,
                body = DeleteDocumentRequest(
                    requestId = requestId,
                    tagName = tagName,
                    taskId = taskId,
                    isDocIdPermanent = isDocIdPermanent
                )
            )
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ApiSuccessFlagResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: ApiSuccessFlagResponse) {
                    baseActivity.showToastMessage("Document Deleted from API")
                    deleteDocFromPhoneAndUpdateUI()
                }

                override fun onError(e: Throwable) {
                    baseActivity.showToastMessage("Unable to delete document from API")
                }

                override fun onComplete() {
                }
            })

        // first call delete document Api than on it's success, call below code
    }

    private fun deleteDocFromPhoneAndUpdateUI() {
        saveAdditionalDocument.value?.docId = null
        saveAdditionalDocument.value?.content = null
        saveAdditionalDocument.value?.fileName = null
        saveAdditionalDocument.value?.docSize = null
        docDeletedUpdateToPhone.triggerWithString("")// passing empty as just need to trigger callback for saving data after this doc data is deleted
        fillUiFromReceivedDoc()
    }

    private fun documentIsUneditable() {
        binding.mandatoryIcon.visibility = View.GONE
        binding.deleteDocCl.visibility = View.GONE
        binding.docTypeTv.setTextColor(
            ContextCompat.getColor(
                baseActivity,
                R.color.color_E2E2E2
            )
        )
    }

    private fun downloadDocument() {
        if (saveAdditionalDocument.value?.content != null && saveAdditionalDocument.value?.fileName != null) {
            downloadFileFromBase64(
                saveAdditionalDocument.value!!.content!!,
                saveAdditionalDocument.value!!.fileName!!
            )
        } else {
            baseActivity.showToastMessage("Unable to download, Base 64 or filename not found")
        }
    }

    private fun downloadFileFromBase64(base64String: String, fileName: String) {
        val mimeExtension = getFileExtensionWithDot(fileName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val fileName = generateUniqueFileName() + mimeExtension
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                //         put(MediaStore.Downloads.MIME_TYPE, mimeType)
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = baseActivity.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                values
            )
            baseActivity.showToastMessage("File $fileName downloaded successfully!")
            if (uri != null) {
                saveFileToUri(uri, base64String)
            } else {
                baseActivity.showToastMessage("Failed to create file")
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    baseActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                baseActivity.showToastMessage("Permission not granted")
                return
            }

            saveFile(base64String, fileName)
        }
    }

    private fun getFileExtensionWithDot(filename: String): String {
        val lastDotIndex = filename.lastIndexOf('.')
        return if (lastDotIndex != -1) filename.substring(lastDotIndex) else ".png"
    }


    private fun saveFile(base64String: String, nameOfFile: String) {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val fileName = generateUniqueFileName() + getFileExtensionWithDot(nameOfFile)
            val outputFile =
                File(baseActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
            outputFile.parentFile?.mkdirs()

            outputFile.outputStream().use { outputStream ->
                outputStream.write(decodedBytes)
            }
            baseActivity.showToastMessage("File $fileName downloaded successfully!")
            notifyFileDownloaded(outputFile.absolutePath)

        } catch (e: Exception) {
            e.printStackTrace()
            baseActivity.showToastMessage("Exception Occurred")
        }
    }


    private fun saveFileToUri(uri: Uri, base64String: String) {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            baseActivity.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(decodedBytes)
            }

            notifyFileDownloaded(uri.toString())

        } catch (e: Exception) {
            e.printStackTrace()
            baseActivity.showToastMessage("Exception Occurred")
        }
    }


    private fun notifyFileDownloaded(filePath: String) {
        val channelId = "download_channel"
        val channelName = "Download Channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager =
                baseActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(baseActivity, channelId)
            .setSmallIcon(R.drawable.download_icon)
            .setContentTitle("File Downloaded")
            .setContentText("File saved to: $filePath")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManager =
            baseActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            100,
            notificationBuilder.build()
        )
    }


    private fun generateUniqueFileName(): String {
        val timestamp = System.currentTimeMillis().toString()
        val randomString =
            UUID.randomUUID().toString().substring(0, 6) // Shorten randomly generated string
        return "$timestamp-$randomString"
    }

    private fun openCameraDocPopup() {

        val firstOptionText = "Camera"
        val secondOptionText = "Document"
        Utilities.showYesNoDialog(
            context = baseActivity,
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
            baseActivity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            baseActivity,
            arrayOf(Manifest.permission.CAMERA),
            requestCameraCode
        )
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, requestCameraCode)
    }

    private fun selectDocAndGetBase64() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type =
//                "image/*|application/msword|application/vnd.openxmlformats-officedocument.wordprocessingml.document|" +
//                        "application/pdf|image/svg+xml|application/vnd.ms-excel|application/vnd.openxmlformats-officedocument.spreadsheetml.sheet|" +
//                        "text/csv"
//        }
//        startActivityForResult(intent, pickDocumentCode)

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
        if (requestCode == pickDocumentCode && resultCode == AppCompatActivity.RESULT_OK) {  //for document
            val uri = data?.data ?: return
            processDocumentAndCallUpload(uri)
        }
        if (requestCode == requestCameraCode && resultCode == AppCompatActivity.RESULT_OK) {   //for camera
            val capturedImage = data?.extras?.get("data") as Bitmap
            val result = getBase64StringAndSizeFromBitmapForCamera(capturedImage)
            val (base64String, imageSize) = result
            saveAdditionalDocument.value ?: return
            saveAdditionalDocument.value!!.taskId = MyApp.localTempVarStore.taskId
            saveAdditionalDocument.value!!.content = base64String
            saveAdditionalDocument.value!!.docSize = "$imageSize KB"
            saveAdditionalDocument.value!!.docId = ""  // need to upload first
            saveAdditionalDocument.value!!.fileName = "Camera Image.png"  // need to upload first

            callUploadDocumentAndUpdateAll3Places(
                saveAdditionalDocument
            )
        }
    }

    @SuppressLint("Range")
    private fun processDocumentAndCallUpload(uri: Uri) {
        try {
            var fileName: String? = null
            var fileSize: Long = 0

            baseActivity.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    fileSize = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
                    if (fileSize > 10 * 1024 * 1024) { // 10 MB in bytes
                        baseActivity.showToastMessage("File size exceeds 10 MB. Please choose a smaller file.")
                        return
                    }
                }
            }
            if (fileName != null && fileSize > 0) {
                baseActivity.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                var inputStream: InputStream? = null
                try {
                    inputStream = baseActivity.contentResolver.openInputStream(uri) ?: return
                    val bytes = inputStream.readBytes()
                    if (saveAdditionalDocument.value == null) {
                        baseActivity.showToastMessage("Unable to upload document!")
                        return
                    }
                    saveAdditionalDocument.value!!.taskId =
                        MyApp.localTempVarStore.taskId
                    saveAdditionalDocument.value!!.content =
                        Base64.encodeToString(bytes, Base64.NO_WRAP)
                    saveAdditionalDocument.value!!.fileName =
                        fileName
                    saveAdditionalDocument.value!!.docSize =
                        (((fileSize / 1024.0).toInt()).toString() + " KB")
                    saveAdditionalDocument.value!!.docId =
                        ""  //id not available yet, need to upload the doc to API
                    callUploadDocumentAndUpdateAll3Places(saveAdditionalDocument)
                } catch (e: Exception) {
                    // Handle exception
                } finally {
                    inputStream?.close()
                }
            } else {
                baseActivity.showToastMessage("Unable to get Document Details!")
                return
            }
        } catch (e: Exception) {
            baseActivity.showToastMessage("Error in parsing document!")
        }
    }

    private fun callUploadDocumentAndUpdateAll3Places(uploadDoc: MutableLiveData<SaveAdditionalDocument>) {
        if (uploadDoc.value == null) {
            baseActivity.showToastMessage("Unable to upload document")
            return
        }
        if (uploadDoc.value!!.content == null) {
            baseActivity.showToastMessage("Please select document before uploading!")
            return
        }

        if (uploadDoc.value!!.tagName != null && uploadDoc.value!!.tagName!!.isEmpty()) {
            baseActivity.showToastMessage("Unable to upload document Tag Name is Empty!")
            return
        }

        val uploadDocumentRequest =
            UploadDocumentRequest(
                content = uploadDoc.value!!.content,
                fileName = uploadDoc.value!!.fileName,
                requestId = MyApp.localTempVarStore.taskResponse?.requestId,
                tagName = uploadDoc.value!!.tagName,
                timeStamp = "",
                userId = KotlinPrefkeeper.lsmUserId?.toIntOrNull() ?: 0
            )

        showProgressBar()
        val api = ApiClient.request
        val observable: Observable<UploadDocumentResponse> =
            api!!.uploadDocument(
                taskId = uploadDoc.value!!.taskId,
                body = uploadDocumentRequest
            )
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<UploadDocumentResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: UploadDocumentResponse) {
                    hideProgressBar()
                    t.docId?.let {
                        uploadDoc.value!!.docId = it
                        baseActivity.showToastMessage("Document Uploaded Successfully!")
                        binding.docName.text =
                            Utilities.getLastChars(saveAdditionalDocument.value?.fileName, 20) ?: ""
                        binding.docSize.text = saveAdditionalDocument.value?.docSize ?: ""
                        saveAdditionalDocument.value?.isDocumentUploadedToAPI =
                            false  //means new Doc
                        saveAdditionalDocument.value?.isDocIdPermanent =
                            false  // used for deleting doc
                    } ?: {
                        baseActivity.showToastMessage("Document ID is blank!")
                    }
                }

                override fun onError(e: Throwable) {
                    hideProgressBar()
                    baseActivity.showToastMessage("Unable to Upload Document!")
                    return
                }

                override fun onComplete() {
                }
            })
    }

    private fun getBase64StringAndSizeFromBitmapForCamera(bitmap: Bitmap): Pair<String?, Int> {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        val stringBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        val imageSize =
            outputStream.size().toLong() // Get size from output stream after compression

        return Pair(stringBase64, ((imageSize).toInt()))
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
}