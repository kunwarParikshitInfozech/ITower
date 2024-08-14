package com.isl.leaseManagement.common.fragments.uploadDoc

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.isl.itower.MyApp
import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.base.BaseFragment
import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.dataClasses.requests.UploadDocumentRequest
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
import java.io.InputStream

class UploadSingleDocumentFragment(private var saveAdditionalDocument: MutableLiveData<SaveAdditionalDocument>) :
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
        if (saveAdditionalDocument.value?.tagName != null) {  // tag name used for enabled/ disabled as well along with uploading to API
            binding.attachDocumentIv.setOnClickListener { openCameraDocPopup() }
            if (saveAdditionalDocument.value?.docContentString64 != null && saveAdditionalDocument.value?.docContentString64 != "") {
                binding.docName.text = saveAdditionalDocument.value?.fileName ?: ""
                binding.docSize.text = saveAdditionalDocument.value?.docSize ?: ""
            }
        } else {
            binding.deleteDocCl.visibility = View.GONE
            binding.docTypeTv.setTextColor(
                ContextCompat.getColor(
                    baseActivity,
                    R.color.color_E2E2E2
                )
            )
        }
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
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
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
            saveAdditionalDocument.value!!.docContentString64 = base64String
            saveAdditionalDocument.value!!.docSize = "$imageSize KB"
            saveAdditionalDocument.value!!.docId = ""  // need to upload first

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
                    saveAdditionalDocument.value!!.docContentString64 =
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
        if (uploadDoc.value!!.docContentString64 == null) {
            baseActivity.showToastMessage("Please select document before uploading!")
            return
        }

        if (uploadDoc.value!!.tagName != null && uploadDoc.value!!.tagName!!.isEmpty()) {
            baseActivity.showToastMessage("Unable to upload document Tag Name is Empty!")
            return
        }

        val uploadDocumentRequest =
            UploadDocumentRequest(
                content = uploadDoc.value!!.docContentString64,
                fileName = uploadDoc.value!!.fileName,
                latitude = 0,
                longitude = 0,
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
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