package com.isl.leaseManagement.baladiya.fieldWork.activities.submit

import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.isl.itower.MyApp
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClasses.responses.BaladiyaNamesListResponse
import com.isl.leaseManagement.dataClasses.responses.FieldWorkStartTaskResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.Utilities
import com.isl.leaseManagement.utils.Utilities.getDateFromISO8601
import com.isl.leaseManagement.utils.Utilities.initializeDropDownWithStringAndIdArray
import com.isl.leaseManagement.utils.Utilities.toIsoString
import infozech.itower.R
import infozech.itower.databinding.ActivitySubmitBaladiyaRequestBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SubmitBaladiyaRequestActivity : BaseActivity() {

    private lateinit var binding: ActivitySubmitBaladiyaRequestBinding
    private var disposable: Disposable? = null
    var baladiyaNameID: Int? = 0
    private lateinit var viewModel: SubmitBaladiyaRequestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_submit_baladiya_request)
        init()
    }

    private fun init() {
        initViewModel()
        setClickListeners()
        initializeDropDowns()
        getStartTaskDataFromRoom()
    }

    private fun initViewModel() {
        val factory = SubmitBaladiyaRequestViewModelFactory(SubmitBaladiyaRequestRepository())
        viewModel =
            ViewModelProvider(this, factory)[SubmitBaladiyaRequestViewModel::class.java]
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.saveAsDraftBtn.setOnClickListener { getDataFromRoomUpdateAndSave(saveToApiAsWell = false) }
        binding.saveToApiAndDraft.setOnClickListener { getDataFromRoomUpdateAndSave(saveToApiAsWell = true) }
    }


    private fun getDataFromRoomUpdateAndSave(saveToApiAsWell: Boolean) {
        disposable = commonDatabase.fieldWorkStartDao()
            .getFieldWorkStartResponseByID(MyApp.localTempVarStore.taskId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->     //from room
                response.data?.let {
                    it.baladiyaName = binding.baladiyaNameSelection.selectedItem.toString()
                    it.baladiyaId = baladiyaNameID?.toString()
                    it.baladiyaApplicationSubmitted =
                        binding.applicationSubmittedSelection.selectedItem.toString()
                    it.baladiyaRequestNumber = binding.baladiyaRequestNumberTvValue.text.toString()
                    it.trackingNumber =
                        binding.trackingNumberTvValue.text.toString()

                    if (binding.applicationSubmissionDateTvValue.text.toString() == getString(R.string.empty_string)) {
                        it.applicationSumissionDate = null
                    } else {
                        it.applicationSumissionDate =
                            toIsoString(binding.applicationSubmissionDateTvValue.text.toString())
                    }
                    saveBackToRoom(response, saveToApiAsWell)
                }
            }, { error ->//
                showToastMessage(error.message.toString())
            })
    }


    private fun saveBackToRoom(
        fieldWorkResponse: FieldWorkStartTaskResponse,
        saveToApiAsWell: Boolean
    ) {
        val d = commonDatabase.fieldWorkStartDao().insert(response = fieldWorkResponse)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                showToastMessage("Saved Successfully to Phone!")
                if (saveToApiAsWell) {
                    saveResponseToBaladiyaApi(fieldWorkResponse)
                }
            }, {
                // Handle error
            })
    }

    private fun saveResponseToBaladiyaApi(fieldWorkResponse: FieldWorkStartTaskResponse) {
        if (fieldWorkResponse.data?.baladiyaApplicationSubmitted != null
            && fieldWorkResponse.data.baladiyaApplicationSubmitted == "Yes"
        ) {
            if (binding.baladiyaRequestNumberTvValue.text.toString() == "") {
                showToastMessage("Enter Request Number!")
                return
            }
            if (binding.applicationSubmissionDateTvValue.text.toString() == "") {
                showToastMessage("Select Submission Date!")
                return
            }
        }

        fieldWorkResponse.data?.taskFlag = AppConstants.TaskFlags.taskBaladiyaRequest
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""
        viewModel.updateBaladiyaResponse(
            userId = lsmUserId,
            taskId = MyApp.localTempVarStore.taskId,
            fieldWorkStartTaskResponse = fieldWorkResponse,
            { response ->
                response?.flag?.let {
                    if (it == "0") {
                        showToastMessage("Data Saved To API")
                    } else {
                        showToastMessage("Unable to save to API")
                    }
                }
            }, {
                showToastMessage("Unable to save to API")
            }
        )

    }

    private fun initializeDropDowns() {
        val baladiyaNameObjectList = MyApp.localTempVarStore.baladiyaNameList
        baladiyaNameObjectList ?: return
        val nameArray = getBaladiyaNames(baladiyaNameObjectList)
        initializeDropDownWithStringAndIdArray(context = this,
            //       stringsArray = resources.getStringArray(R.array.yes_no_drop_down),
            stringsArray = nameArray,
            spinner = binding.baladiyaNameSelection,
            commonInterface = object :
                ClickInterfaces.CommonInterface {
                override fun triggerWithString(string: String) {//no use
                }

                override fun triggerWithInt(int: Int) {
                    val pos = int - 1  // since first is choose an option
                    if (pos < 0) {
                        showToastMessage("Selection invalid")
                        return
                    }
                    baladiyaNameID = (getBaladiyaIdAtPosition(
                        baladiyaNameObjectList,
                        pos
                    ))
                }
            }
        )

        initializeDropDownWithStringAndIdArray(context = this,
            stringsArray = resources.getStringArray(R.array.yes_no_drop_down),
            spinner = binding.applicationSubmittedSelection,
            commonInterface = object :
                ClickInterfaces.CommonInterface {
                override fun triggerWithString(string: String) {//no use
                    if (string == "Yes") {
                        makeFormEnabled()
                    } else {
                        makeFormDisabled()
                    }
                }

                override fun triggerWithInt(int: Int) {//no use
                }
            }
        )
    }

    private fun makeFormDisabled() {
        binding.baladiyaRequestNumberTvValue.background =
            ContextCompat.getDrawable(this, R.drawable.bg_filled_grey_rounded)
        binding.trackingNumberTvValue.background =
            ContextCompat.getDrawable(this, R.drawable.bg_filled_grey_rounded)
        binding.applicationSubmissionDateTvValue.background =
            ContextCompat.getDrawable(this, R.drawable.bg_filled_grey_rounded)

        binding.baladiyaRequestNumberTvValue.isEnabled = false
        binding.trackingNumberTvValue.isEnabled = false
        binding.applicationSubmissionDateTvValue.setOnClickListener(null)

        binding.baladiyaRequestNumberTvValue.setText(getString(R.string.empty_string))
        binding.trackingNumberTvValue.setText(getString(R.string.empty_string))
        binding.applicationSubmissionDateTvValue.text = getString(R.string.empty_string)

    }

    private fun makeFormEnabled() {
        binding.baladiyaRequestNumberTvValue.background =
            ContextCompat.getDrawable(this, R.drawable.btn_light_grey_border_bg)
        binding.trackingNumberTvValue.background =
            ContextCompat.getDrawable(this, R.drawable.btn_light_grey_border_bg)
        binding.applicationSubmissionDateTvValue.background =
            ContextCompat.getDrawable(this, R.drawable.btn_light_grey_border_bg)

        binding.baladiyaRequestNumberTvValue.isEnabled = true
        binding.trackingNumberTvValue.isEnabled = true
        binding.applicationSubmissionDateTvValue.setOnClickListener {
            Utilities.showDatePickerAndFillDate(
                it as TextView,
                this
            )
        }
    }

    private fun getBaladiyaIdAtPosition(response: BaladiyaNamesListResponse, position: Int): Int? {
        return response.getOrNull(position)?.baladiyaId
    }

    private fun getPositionByBaladiyaId(
        response: BaladiyaNamesListResponse,
        baladiyaId: Int
    ): Int? {
        return response.indexOfFirst { it.baladiyaId == baladiyaId }.takeIf { it >= 0 }
    }

    private fun getBaladiyaNames(response: BaladiyaNamesListResponse): Array<String> {
        return response.mapNotNull { it.baladiyaName }.toTypedArray()
    }

    private fun getStartTaskDataFromRoom() {
        val fieldWorkStartDao = commonDatabase.fieldWorkStartDao()
        disposable = fieldWorkStartDao.getFieldWorkStartResponseByID(MyApp.localTempVarStore.taskId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ baladiyaStartResponse ->
                fillStartTaskData(baladiyaStartResponse)
            }, { error ->//
                showToastMessage(error.message.toString())
            })
    }

    private fun fillStartTaskData(baladiyaStartResponse: FieldWorkStartTaskResponse) {

        baladiyaStartResponse.data?.baladiyaRequestNumber?.let {
            binding.baladiyaRequestNumberTvValue.setText(it)
        }
        baladiyaStartResponse.data?.trackingNumber?.let {
            binding.trackingNumberTvValue.setText(it)
        }
        baladiyaStartResponse.data?.applicationSumissionDate?.let {
            binding.applicationSubmissionDateTvValue.text = getDateFromISO8601(it)
        }

        baladiyaStartResponse.data?.baladiyaApplicationSubmitted?.let {
            if (it == "Yes") {
                binding.applicationSubmittedSelection.setSelection(1)  //0 is for choose an option
                makeFormEnabled()
            } else {
                binding.applicationSubmittedSelection.setSelection(2)
                makeFormDisabled()
            }
        }

        baladiyaStartResponse.data?.baladiyaId?.let {
            val baladiyaNameObjectList = MyApp.localTempVarStore.baladiyaNameList
            baladiyaNameObjectList ?: return
            getPositionByBaladiyaId(
                baladiyaNameObjectList,
                (it.toInt())
            )?.let { selectionPos ->
                binding.baladiyaNameSelection.setSelection(selectionPos + 1)  //+1 for choose an option
            }

        }
    }
}