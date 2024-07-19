package com.isl.leaseManagement.activities.addtionalDocs

import android.app.Dialog
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.view.Gravity
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.isl.itower.MyApp
import com.isl.leaseManagement.activities.addAdditionalDoc.AddAdditionalDocumentActivity
import com.isl.leaseManagement.activities.requestDetails.RequestDetailsActivity
import com.isl.leaseManagement.adapters.AdditionalDocumentsListAdapter
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClass.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.room.entity.SaveAdditionalDocumentPOJO
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities
import infozech.itower.R
import infozech.itower.databinding.ActionsPopupBinding
import infozech.itower.databinding.ActivityAdditonalDocumentsBinding
import infozech.itower.databinding.DocInfoPopupBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class AdditionalDocumentsActivity : BaseActivity() {
    private lateinit var binding: ActivityAdditonalDocumentsBinding
    private var currentTaskId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_additonal_documents)
        init()
    }

    private fun init() {
        currentTaskId = MyApp.localTempVarStore.taskId
        getAdditionalDocListOfThisTask()
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.actionsBtn.setOnClickListener { showActionsPopup() }
    }

    private fun getAdditionalDocListOfThisTask() {
        lifecycleScope.launch(Dispatchers.IO) {
            val documentsList =
                MyApp.getMyDatabase().saveAdditionalDocumentDao()
                    .getAllSavedDocumentsOfATask(currentTaskId.toString()) as ArrayList<SaveAdditionalDocumentPOJO>
            showAdditionalDocuments(documentsList)
        }
    }

    private fun convertPOJOListToDocumentList(pojoList: ArrayList<SaveAdditionalDocumentPOJO>): ArrayList<SaveAdditionalDocument> {
        val documentList = ArrayList<SaveAdditionalDocument>()
        for (pojo in pojoList) {
            val document = SaveAdditionalDocument(
                taskId = pojo.getTaskId() ?: 0, // Handle potential null taskId
                fileName = pojo.getDocName(),
                docSize = pojo.getDocSize(),
                docContentString64 = pojo.getDocContentString64()
            )
            documentList.add(document)
        }
        return documentList
    }

    private fun showAdditionalDocuments(documentsList: ArrayList<SaveAdditionalDocumentPOJO>) {
        val arrayOfSaveAdditionalDocument = convertPOJOListToDocumentList(documentsList)
        val additionalDocumentAdapter =
            AdditionalDocumentsListAdapter(arrayOfSaveAdditionalDocument, this,
                object : ClickInterfaces.AdditionalDocumentList {
                    override fun docInfo(saveAdditionalDocument: SaveAdditionalDocument) {
                        docInfoClicked(saveAdditionalDocument)
                    }

                    override fun docDownload(saveAdditionalDocument: SaveAdditionalDocument) {
                        if (saveAdditionalDocument.docContentString64 != null) {
                            downloadFileFromBase64(saveAdditionalDocument.docContentString64!!)
                        } else {
                            showToastMessage("Unable to download, Base 64 not found")
                        }

                    }
                })
        binding.rvDocumentList.layoutManager = LinearLayoutManager(this)
        binding.rvDocumentList.adapter = additionalDocumentAdapter
    }

    fun downloadFileFromBase64(base64String: String) {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val context = this
        val fileName = generateUniqueFileName()
        val outputFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (outputFile.exists()) {
            outputFile.delete()
        }
        val outputStream = FileOutputStream(outputFile)
        outputStream.write(decodedBytes)
        outputStream.close()

        // Inform the user about the downloaded file (optional)
        showToastMessage("File downloaded: $fileName") // Or use a Snackbar
    }

    private fun generateUniqueFileName(): String {
        val timestamp = System.currentTimeMillis().toString()
        val randomString =
            UUID.randomUUID().toString().substring(0, 6) // Shorten randomly generated string
        return "$timestamp-$randomString"
    }

    private fun docInfoClicked(saveAdditionalDocument: SaveAdditionalDocument) {
        val dialog = Dialog(this)
        val binding = DocInfoPopupBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        val heightInPixels = Utilities.dpToPx(this, 480)
        layoutParams.height = heightInPixels
        layoutParams.gravity = Gravity.BOTTOM
        dialog.window!!.attributes = layoutParams
        dialog.show()
        binding.closeTv.setOnClickListener {
            dialog.dismiss()
        }
        binding.documentNameValue.text = saveAdditionalDocument.fileName ?: ""
    }

    private fun showActionsPopup() {
        val dialog = Dialog(this)
        val binding = ActionsPopupBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        val heightInPixels = Utilities.dpToPx(this, 480)
        layoutParams.height = heightInPixels
        layoutParams.gravity = Gravity.BOTTOM
        dialog.window!!.attributes = layoutParams
        dialog.show()
        binding.closeTv.setOnClickListener {
            dialog.dismiss()
        }
        binding.requestDetailsTv.setOnClickListener {
            launchActivity(RequestDetailsActivity::class.java)
        }
        binding.addAdditionalDocTv.setOnClickListener {
            launchActivity(AddAdditionalDocumentActivity::class.java)
        }
    }
}