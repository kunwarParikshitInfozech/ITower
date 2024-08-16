package com.isl.leaseManagement.common.activities.addAdditionalDoc

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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.isl.itower.MyApp
import com.isl.leaseManagement.common.adapters.DeleteDocumentAdapter
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.dataClasses.requests.UploadDocumentRequest
import com.isl.leaseManagement.room.entity.common.SaveAdditionalDocumentPOJO
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities.showYesNoDialog
import infozech.itower.R
import infozech.itower.databinding.ActivityAddAdditionalDocumentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AddAdditionalDocumentActivity : BaseActivity() {
    private lateinit var binding: ActivityAddAdditionalDocumentBinding
    private val pickDocumentCode = 1;
    private val requestCameraCode = 2;
    private lateinit var viewModel: AddAdditionalDocumentViewModel
    private var stringBase64: String? = null
    private var deleteDocumentAdapter: DeleteDocumentAdapter? = null
    private var documentList = ArrayList<SaveAdditionalDocument>()
    private var maxDocAllowed = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_additional_document)
        init()
    }

    private fun init() {
        binding.additionalDocScrollView.isNestedScrollingEnabled = true
        binding.rvDeleteDocument.isNestedScrollingEnabled = true
        checkHowManyDocsAreUploaded()
        initializeDeleteDocAdapter()
        val factory =
            AddAdditionalDocumentViewModelFactory(
                AddAdditionalDocumentRepository()
            )
        viewModel = ViewModelProvider(this, factory)[AddAdditionalDocumentViewModel::class.java]
        setClickListeners()
    }

    private fun checkHowManyDocsAreUploaded() {
        lifecycleScope.launch(Dispatchers.IO) {
            val documentsList =
                MyApp.getMyDatabase().saveAdditionalDocumentDao()
                    .getAllSavedDocumentsOfATask(MyApp.localTempVarStore.taskId.toString())
            maxDocAllowed -= documentsList.size
        }
    }

    private fun initializeDeleteDocAdapter() {
        binding.rvDeleteDocument.layoutManager = LinearLayoutManager(this)
        deleteDocumentAdapter = DeleteDocumentAdapter(
            documentList, this,
            object : ClickInterfaces.AddAdditionalDocument {
                override fun deleteDocument(saveAdditionalDocument: SaveAdditionalDocument) {
                    deleteDocumentClicked(saveAdditionalDocument)
                }
            },
        )
        binding.rvDeleteDocument.adapter = deleteDocumentAdapter
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.addAttachmentIv.setOnClickListener {
            openCameraDocPopup()
        }
        binding.saveBtn.setOnClickListener {
            saveDocuments(documentList)
        }
    }

    private fun saveDocuments(documentList: List<SaveAdditionalDocument>) {
        if (documentList.isEmpty()) {
            showToastMessage("Select a document before uploading!")
            return
        }
        for (document in documentList) {
            var insertedRowId = 0L
            val documentPOJO = convertDocumentToPOJO(document) // Implement conversion function
            lifecycleScope.launch(Dispatchers.IO) {
                insertedRowId =
                    MyApp.getMyDatabase().saveAdditionalDocumentDao().insertDocument(documentPOJO)
                delay(100)
                finish()
            }
        }
        showToastMessage("Document Saved Successfully")
    }

    private fun convertDocumentToPOJO(document: SaveAdditionalDocument): SaveAdditionalDocumentPOJO {
        val docName = document.fileName ?: ""
        val docUploadId = document.docId ?: ""

        return SaveAdditionalDocumentPOJO(
            document.docContentString64!!,
            docName,
            document.docSize ?: "",
            docUploadId,
            document.taskId
        )
    }

    private fun openCameraDocPopup() {
        if (documentList.size < maxDocAllowed) {
            val firstOptionText = "Camera" // Change this to your desired text
            val secondOptionText = "Document" // Change this to your desired text
            showYesNoDialog(
                context = this,
                message = "Select Camera OR Document",
                firstOptionName = firstOptionText,
                secondOptionName = secondOptionText,
                optionSelection = object : ClickInterfaces.TwoOptionSelection {
                    override fun option1Selected() {
                        openCameraAndGetBase64String()
                    }

                    override fun option2Selected() {
                        pickDocumentAndGetBase64()
                    }

                }
            )
        } else {
            showToastMessage("5 Documents are already selected")
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
            callUploadDocumentAndShowDeleteRv(
                SaveAdditionalDocument(
                    taskId = MyApp.localTempVarStore.taskId,
                    docContentString64 = base64String,
                    docSize = "$imageSize KB",
                    fileName = "Camera Image"
                )
            )
        }
    }

    private fun getBase64StringAndSizeFromBitmapForCamera(bitmap: Bitmap): Pair<String?, Int> {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            80,
            outputStream
        )   //around 30 percent compression
        val byteArray = outputStream.toByteArray()
        val stringBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        val imageSize =
            outputStream.size().toLong() // Get size from output stream after compression

        return Pair(stringBase64, ((imageSize).toInt()))
    }


    private fun pickDocumentAndGetBase64() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, pickDocumentCode)
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
                        // Show toast message using Toast or a custom snackbar
                        showToastMessage("File size exceeds 10 MB. Please choose a smaller file.")
                        return@use // Exit the cursor use block if file is too large
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
                        docContentString64 = stringBase64,
                        fileName = fileName,
                        docSize = (((fileSize / 1024.0).toInt()).toString() + " KB"),
                        docId = ""  //id not available yet, need to upload the doc to API
                    )
                    callUploadDocumentAndShowDeleteRv(saveAdditionalDocument)
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


    private fun callUploadDocumentAndShowDeleteRv(saveAdditionalDocument: SaveAdditionalDocument) {
        val taskId = MyApp.localTempVarStore.taskId
        if (saveAdditionalDocument.docContentString64 == null) {
            showToastMessage("Please select document before uploading!")
            return
        }
        val uploadDocumentRequest =
            UploadDocumentRequest(
                content = saveAdditionalDocument.docContentString64,
                fileName = saveAdditionalDocument.fileName,
                latitude = 0,
                longitude = 0,
                requestId = MyApp.localTempVarStore.taskResponse?.requestId,
                tagName = AppConstants.DocsTagNames.additionalDocumentTagName,
                timeStamp = "",
                userId = 123
            )
        binding.addAttachmentIv.setOnClickListener(null)
        showProgressBar()
        viewModel.uploadDocument(
            { successResponse ->
                successResponse?.let { response ->
                    hideProgressBar()
                    if (response.docId != null) {
                        saveAdditionalDocument.docId = response.docId
                        showToastMessage("Document Uploaded Successfully!")
                        showDeleteDocumentRecyclerView(saveAdditionalDocument)
                    } else {
                        showToastMessage("Document ID received empty!")
                    }
                }
                binding.addAttachmentIv.setOnClickListener {
                    openCameraDocPopup()
                }
            },
            { errorMessage ->
                hideProgressBar()
                showToastMessage("Unable to upload Document!")
                binding.addAttachmentIv.setOnClickListener {
                    openCameraDocPopup()
                }
            }, taskId = taskId,
            body =
            uploadDocumentRequest
        )
    }

    private fun showDeleteDocumentRecyclerView(saveAdditionalDocument: SaveAdditionalDocument) {
        documentList.add(saveAdditionalDocument)
        deleteDocumentAdapter?.notifyItemInserted(documentList.size - 1)
    }

    private fun deleteDocumentClicked(saveAdditionalDocument: SaveAdditionalDocument) {
        val indexOfObjectToDelete =
            documentList.indexOfFirst { it.docSize == saveAdditionalDocument.docSize } // Find index with matching taskID
        if (indexOfObjectToDelete != -1) {
            documentList.removeAt(indexOfObjectToDelete)
            deleteDocumentAdapter?.notifyItemRemoved(indexOfObjectToDelete)
        } else {
            showToastMessage("Unable to find document!")
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

}