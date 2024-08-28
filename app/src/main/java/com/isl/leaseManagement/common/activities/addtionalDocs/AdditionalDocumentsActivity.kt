package com.isl.leaseManagement.common.activities.addtionalDocs

import android.Manifest
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.Gravity
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.isl.itower.MyApp
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.common.activities.addAdditionalDoc.AddAdditionalDocumentActivity
import com.isl.leaseManagement.common.adapters.AdditionalDocumentsListAdapter
import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.paymentProcess.activities.requestDetails.RequestDetailsActivity
import com.isl.leaseManagement.room.entity.common.SaveAdditionalDocumentPOJO
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities
import com.isl.leaseManagement.utils.Utilities.getLastChars
import infozech.itower.R
import infozech.itower.databinding.ActionsPopupBinding
import infozech.itower.databinding.ActivityAdditonalDocumentsBinding
import infozech.itower.databinding.DocInfoPopupBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class AdditionalDocumentsActivity : BaseActivity() {
    private lateinit var binding: ActivityAdditonalDocumentsBinding
    private var currentTaskId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_additonal_documents)
    }

    override fun onResume() {
        super.onResume()
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
            withContext(Dispatchers.Main) {
                showAdditionalDocuments(documentsList)
            }
        }
    }

    private fun convertPOJOListToDocumentList(pojoList: ArrayList<SaveAdditionalDocumentPOJO>): ArrayList<SaveAdditionalDocument> {
        val documentList = ArrayList<SaveAdditionalDocument>()
        for (pojo in pojoList) {
            val document = SaveAdditionalDocument(
                taskId = pojo.taskId ?: 0, // Handle potential null taskId
                fileName = pojo.docName,
                docSize = pojo.docSize,
                content = pojo.docContentString64,
                docId = pojo.docUploadId
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
                        if (saveAdditionalDocument.content != null && saveAdditionalDocument.fileName != null) {
                            downloadFileFromBase64(
                                saveAdditionalDocument.content!!,
                                saveAdditionalDocument.fileName!!
                            )
                        } else {
                            showToastMessage("Unable to download, Base 64 or filename not found")
                        }

                    }
                })
        binding.rvDocumentList.layoutManager = LinearLayoutManager(this)
        binding.rvDocumentList.adapter = additionalDocumentAdapter
    }

    fun getFileExtensionWithDot(filename: String): String {
        val lastDotIndex = filename.lastIndexOf('.')
        return if (lastDotIndex != -1) filename.substring(lastDotIndex) else ".png"
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

            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            showToastMessage("File $fileName downloaded successfully!")
            if (uri != null) {
                saveFileToUri(uri, base64String)
            } else {
                showToastMessage("Failed to create file")
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showToastMessage("Permission not granted")
                return
            }

            saveFile(base64String, fileName)
        }
    }

    private fun saveFile(base64String: String, nameOfFile: String) {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val fileName = generateUniqueFileName() + getFileExtensionWithDot(nameOfFile)
            val outputFile =
                File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
            outputFile.parentFile?.mkdirs()

            outputFile.outputStream().use { outputStream ->
                outputStream.write(decodedBytes)
            }
            showToastMessage("File $fileName downloaded successfully!")
            notifyFileDownloaded(outputFile.absolutePath)

        } catch (e: Exception) {
            e.printStackTrace()
            showToastMessage("Exception Occurred")
        }
    }


    private fun saveFileToUri(uri: Uri, base64String: String) {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(decodedBytes)
            }

            notifyFileDownloaded(uri.toString())

        } catch (e: Exception) {
            e.printStackTrace()
            showToastMessage("Exception Occurred")
        }
    }


    private fun notifyFileDownloaded(filePath: String) {
        val channelId = "download_channel"
        val channelName = "Download Channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.download_icon)
            .setContentTitle("File Downloaded")
            .setContentText("File saved to: $filePath")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
        saveAdditionalDocument.fileName?.let {
            binding.documentNameValue.text = getLastChars(it, 28)
        }
        binding.uploadDateTimeValue.text = saveAdditionalDocument.dateOfSaving ?: ""
        MyApp.localTempVarStore?.paymentStartTaskResponse?.data?.paymentMethod?.let {
            binding.documentTypeValue.text = it
        }
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
            dialog.dismiss()
            launchActivity(RequestDetailsActivity::class.java)
        }
        binding.taskDetailTv.setOnClickListener {
            dialog.dismiss()
            MyApp.localTempVarStore.taskResponse?.let { it1 ->
                Utilities.showTaskDetailsPopupWithoutStart(
                    this,
                    it1
                )
            }
        }
        binding.addAdditionalDocTv.setOnClickListener {
            dialog.dismiss()
            launchActivity(AddAdditionalDocumentActivity::class.java)
        }
    }
}