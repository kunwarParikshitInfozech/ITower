package com.isl.leaseManagement.bts.captureCandidate.capturedCandidate

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.isl.itower.MyApp
import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.bts.adapter.CapturedCandidateAdapter
import com.isl.leaseManagement.bts.captureCandidate.captureNewCandidate.CaptureNewCandidateActivity
import com.isl.leaseManagement.dataClasses.requests.DeleteCandidateRequest
import com.isl.leaseManagement.dataClasses.responses.ApiSuccessFlagResponse
import com.isl.leaseManagement.dataClasses.responses.ExistingCandidateListResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.ActionButtonMethods
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.MessageConstants
import com.isl.leaseManagement.utils.Utilities.showYesNoDialog
import infozech.itower.R
import infozech.itower.databinding.ActivityCapturedCandidateBinding
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CapturedCandidateActivity : BaseActivity() {

    private lateinit var binding: ActivityCapturedCandidateBinding
    private var disposable: Disposable? = null
    private val candidatesListSelected: ArrayList<ExistingCandidateListResponse.ExistingCandidateListResponseItem> =
        arrayListOf()
    private val api = ApiClient.request
    private var capturedCandidateAdapter: CapturedCandidateAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_captured_candidate)
        init()
    }

    private fun init() {
        getAllCandidatesAndProceed()
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.saveBtn.setOnClickListener {
            reloadDataAndGetToDefaultMode()
        }
        binding.deleteIv.setOnClickListener {
            showYesNoDialog(this,
                title = getString(R.string.are_you_sure_you_want_to_delete_this_candidate),
                message = getString(R.string.this_candidate_will_be_deleted_permanently),
                optionSelection = object : ClickInterfaces.TwoOptionSelection {
                    override fun option1Selected() {
                        deleteCandidatesFromApiAndPhone(candidatesListSelected)
                    }

                    override fun option2Selected() {//not needed here
                    }

                }
            )
        }
        binding.selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectAll()
            } else {
                unSelectAll()
            }
        }
        binding.actionBtn.setOnClickListener {
            ActionButtonMethods.Actions.showActionPopup(
                this, ActionButtonMethods.ActionOpeningProcess.BtsCaptureCandidate
            )
        }
    }

    private fun selectAll() {
        capturedCandidateAdapter?.selectAllCandidates()
        disposable =
            commonDatabase.existingCandidateListResponseItemDao()
                .getAllCandidates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ candidatesList ->
                    candidatesListSelected.clear()
                    candidatesListSelected.addAll(candidatesList)
                    binding.candidateSelectedTvCount.text = candidatesListSelected.size.toString()
                }, { error ->
                })
    }

    private fun unSelectAll() {
        capturedCandidateAdapter?.unselectAllCandidates()
        candidatesListSelected.clear()
        binding.candidateSelectedTvCount.text = candidatesListSelected.size.toString()
    }

    private fun reloadDataAndGetToDefaultMode() {
        candidatesListSelected.clear()
        openOrHideSelectAllMode(false)
        getAllCandidatesAndProceed()
    }

    private fun deleteCandidatesFromApiAndPhone(candidatesListSelected: ArrayList<ExistingCandidateListResponse.ExistingCandidateListResponseItem>) {
        val deleteCandidates: List<DeleteCandidateRequest.DeleteCandidate?> = candidatesListSelected
            .filter { it.propertyId != null && it.candidateId != null }  // Ensure both candidateId and propertyId are not null
            .map { candidate ->
                DeleteCandidateRequest.DeleteCandidate(
                    candidateId = candidate.candidateId,
                    propertyId = candidate.propertyId
                )
            }

        val requestID = MyApp.localTempVarStore.taskResponse?.requestId
        val requestBody = DeleteCandidateRequest(
            requestId = requestID,
            selectedProperties = deleteCandidates
        )

        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""  // was used earlier
        val observable: Observable<ApiSuccessFlagResponse> =
            api!!.deleteCandidate(
                tenantId = KotlinPrefkeeper.leaseManagementUserID!!,
                taskId = MyApp.localTempVarStore.taskId,
                body = requestBody
            )
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :
                Observer<ApiSuccessFlagResponse> {
                override fun onSubscribe(d: Disposable) {
                    showProgressBar()
                }

                override fun onNext(linkExistingCandidateResponse: ApiSuccessFlagResponse) {
                    hideProgressBar()
                    deleteCandidatesFromPhone()
                }

                override fun onError(e: Throwable) {
                    hideProgressBar()
                    showToastMessage(MessageConstants.ErrorMessages.unableToDeleteExistingCandidate)
                }

                override fun onComplete() {
                }
            })
    }

    private fun deleteCandidatesFromPhone() {
        val propertyIds: List<String> = candidatesListSelected
            .filter { it.propertyId != null }  // Ensure propertyId is not null
            .map { it.propertyId!! }  // Extract the propertyId

        disposable =
            commonDatabase.existingCandidateListResponseItemDao()
                .deleteByPropertyIds(propertyIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    reloadDataAndGetToDefaultMode()
                }, { _ ->
                })
    }

    private fun getAllCandidatesAndProceed() {
        disposable =
            commonDatabase.existingCandidateListResponseItemDao()
                .getAllCandidates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ candidatesList ->
                    setAdapter(candidatesList)
                }, { error ->
                })
    }

    private fun setAdapter(candidatesList: List<ExistingCandidateListResponse.ExistingCandidateListResponseItem>) {
        capturedCandidateAdapter = CapturedCandidateAdapter(
            candidatesList = ArrayList(candidatesList),
            this,
            object : ClickInterfaces.ExistingCandidateSelection {
                override fun candidateClicked(selectedCandidateData: ExistingCandidateListResponse.ExistingCandidateListResponseItem) {
                    candidateSelected(selectedCandidateData)
                }

                override fun candidateNameSelected(selectedCandidateData: ExistingCandidateListResponse.ExistingCandidateListResponseItem) {
                    val intent = Intent(
                        this@CapturedCandidateActivity,
                        CaptureNewCandidateActivity::class.java
                    )
                    intent.putExtra(
                        AppConstants.IntentKeys.candidateID,
                        selectedCandidateData.candidateId
                    )
                    intent.putExtra(
                        AppConstants.IntentKeys.candidateRejectionRemarks,
                        selectedCandidateData.remarks
                    )
                    launchActivityWithIntent(intent)
                }
            })

        binding.noCandidateText.visibility =
            if (candidatesList.isEmpty()) View.VISIBLE else View.GONE
        binding.llSave.visibility = if (candidatesList.isEmpty()) View.GONE else View.VISIBLE


        binding.capturedCandidatesRv.layoutManager = LinearLayoutManager(this)
        binding.capturedCandidatesRv.adapter = capturedCandidateAdapter

    }

    private fun candidateSelected(selectedCandidateData: ExistingCandidateListResponse.ExistingCandidateListResponseItem) {
        val exists =
            candidatesListSelected.any { it.propertyId == selectedCandidateData.propertyId }
        candidatesListSelected.removeAll { it.propertyId == selectedCandidateData.propertyId }
        if (!exists) {
            candidatesListSelected.add(selectedCandidateData)
        }
        binding.candidateSelectedTvCount.text = candidatesListSelected.size.toString()
        openOrHideSelectAllMode(true)
    }

    private fun openOrHideSelectAllMode(openSelectDelete: Boolean) {
        binding.deleteIv.visibility = if (openSelectDelete) View.VISIBLE else View.GONE
        binding.selectCandidateCl.visibility = if (openSelectDelete) View.VISIBLE else View.GONE
        binding.captureCandidateDetailsTv.visibility =
            if (openSelectDelete)
                View.GONE
            else
                View.VISIBLE
    }


    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

}