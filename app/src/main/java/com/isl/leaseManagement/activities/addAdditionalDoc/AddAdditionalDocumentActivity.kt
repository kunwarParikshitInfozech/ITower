package com.isl.leaseManagement.activities.addAdditionalDoc

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.isl.itower.MyApp
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClass.requests.UploadDocumentRequest
import com.isl.leaseManagement.utils.AppConstants
import infozech.itower.BuildConfig
import infozech.itower.R
import infozech.itower.databinding.ActivityAddAdditionalDocumentBinding
import java.io.File
import java.io.InputStream

class AddAdditionalDocumentActivity : BaseActivity() {
    private lateinit var binding: ActivityAddAdditionalDocumentBinding
    private val pickDocumentCode = 1;
    private lateinit var viewModel: AddAdditionalDocumentViewModel
    private var stringBase64: String? = null

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
        binding.addAttachmentIv.setOnClickListener { pickDocument() }
        binding.saveBtn.setOnClickListener { uploadDocument() }
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
                tagName = AppConstants.KeyWords.additionalDocumentTagName,
                timeStamp = "",
                userId = 123
            )

        showProgressBar()
        viewModel.uploadDocument(
            { successResponse ->
                successResponse?.let {
                    hideProgressBar()
                    it.docId?.let {
                        //got success
                        showToastMessage("Document Uploaded Successfully!")
                        finish()
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
            } catch (e: Exception) {       //   exception case
            } finally {
                inputStream?.close()
            }
        }
    }


    private fun openPdfFromBase64(base64String: String) {
        val bytes = Base64.decode(base64String, Base64.NO_WRAP)
        val pdfFile = File(this.cacheDir, "temp.pdf")  // Create temporary file in cache
        pdfFile.writeBytes(bytes)

        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".provider",
            pdfFile
        )
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)

        // Delete temporary file after opening (optional)
        // pdfFile.delete()
    }

}