package com.isl.leaseManagement.activities.addAdditionalDoc

import android.Manifest
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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.isl.itower.MyApp
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClass.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.dataClass.requests.UploadDocumentRequest
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.Utilities.getLastChars
import com.isl.leaseManagement.utils.Utilities.showYesNoDialog
import infozech.itower.R
import infozech.itower.databinding.ActivityAddAdditionalDocumentBinding
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AddAdditionalDocumentActivity : BaseActivity() {
    private lateinit var binding: ActivityAddAdditionalDocumentBinding
    private val pickDocumentCode = 1;
    private val REQUEST_CODE_CAMERA = 2;
    private lateinit var viewModel: AddAdditionalDocumentViewModel
    private var stringBase64: String? = null
    private val docList = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_additional_document)
        init()
    }

    private fun init() {
        val factory = AddAdditionalDocumentViewModelFactory(AddAdditionalDocumentRepository())
        viewModel = ViewModelProvider(this, factory)[AddAdditionalDocumentViewModel::class.java]
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.addAttachmentIv.setOnClickListener {
            openCameraDocPopup()
        }
        binding.saveBtn.setOnClickListener {
            KotlinPrefkeeper.additionalDocIdsForSubmitApi = docList.toIntArray()
//            KotlinPrefkeeper.additionalDocDataArray =
//                SaveAdditionalDocumentsArray(saveAdditionalDocumentArray)
            finish()
        }
    }

    private fun openCameraDocPopup() {
        if (docList.size < 5) {
            val firstOptionText = "Camera" // Change this to your desired text
            val secondOptionText = "Document" // Change this to your desired text
            showYesNoDialog(
                firstOptionName = firstOptionText,
                secondOptionName = secondOptionText,
                context = this, // Assuming you're calling from an activity, use 'this'
                title = "Choose Camera or file",
                message = "Select",
                firstOptionClicked = {
                    openCameraAndGetBase64String()
                },
                secondOptionClicked = {
                    pickDocument()
                }
            )
        } else {
            showToastMessage("5 Documents are already selected")
        }
    }

    private fun uploadDocument() {
        val taskId = MyApp.localTempVarStore.taskId
        //    val taskId = 3220  // for testing
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
                tagName = AppConstants.KeyWords.additionalDocumentTagName,
                timeStamp = "",
                userId = 123
            )

        showProgressBar()
        viewModel.uploadDocument(
            { successResponse ->
                successResponse?.let { response ->
                    hideProgressBar()
                    response.docId?.let {
                        showToastMessage("Document Uploaded Successfully!")
                        docList.add(it.toInt())

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

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }


    private fun pickDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, pickDocumentCode)
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
            processDocDataAndGetBase64(uri)
        }
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            val capturedImage = data?.extras?.get("data") as Bitmap
            val base64Image = getBase64StringFromBitmapForCamera(capturedImage)
            storeBase64AndShowDeleteUI(base64 = base64Image, name = "Camera", size = "Unknown")
        }
    }

    private fun getBase64StringFromBitmapForCamera(bitmap: Bitmap): String? {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        stringBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return stringBase64
    }

    private fun processDocDataAndGetBase64(uri: Uri) {
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

    private val saveAdditionalDocumentArray = arrayListOf<SaveAdditionalDocument>()

    private fun storeBase64AndShowDeleteUI(name: String?, size: String?, base64: String?) {
        if (base64 == null) {
            showToastMessage("Unable to get content string")
            return
        }
        val saveAdditionalDocument = SaveAdditionalDocument(
            docName = name, docSize = size, docContentString64 = base64
        )

        saveAdditionalDocumentArray.add(saveAdditionalDocument)

        val inflater = LayoutInflater.from(this)  // Assuming you're in an activity
        val linearLayout = inflater.inflate(
            R.layout.delete_document_layout,
            binding.llDeleteDocUI,
            false
        ) as LinearLayout
        binding.llDeleteDocUI.addView(linearLayout)
        val docName = linearLayout.findViewById<TextView>(R.id.docName)
        val docSize = linearLayout.findViewById<TextView>(R.id.docSize)
        val docDelete = linearLayout.findViewById<TextView>(R.id.deleteDoc)

        saveAdditionalDocument.docName?.let {
            docName.text = getLastChars(it, 16)
        }
        size?.let {
            docSize.text = getLastChars(it, 10)
        }
        uploadDocument()
        docDelete.setOnClickListener {
            binding.llDeleteDocUI.removeView(linearLayout)
            if (docList.isNotEmpty()) {
                docList.removeLast()
            }
            if (saveAdditionalDocumentArray.isNotEmpty()) {
                saveAdditionalDocumentArray.removeLast()
            }
        }
    }



}