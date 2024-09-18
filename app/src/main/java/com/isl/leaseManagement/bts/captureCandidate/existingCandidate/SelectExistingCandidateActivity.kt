package com.isl.leaseManagement.bts.captureCandidate.existingCandidate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isl.itower.MyApp
import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.bts.adapter.ExistingCandidateListAdapter
import com.isl.leaseManagement.bts.captureCandidate.capturedCandidate.CapturedCandidateActivity
import com.isl.leaseManagement.dataClasses.requests.LinkExistingCandidateRequest
import com.isl.leaseManagement.dataClasses.responses.ExistingCandidateListResponse
import com.isl.leaseManagement.dataClasses.responses.LinkExistingCandidateResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.ActionButtonMethods
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.ClickInterfaces
import com.isl.leaseManagement.utils.MessageConstants
import com.isl.leaseManagement.utils.MessageConstants.ErrorMessages.onlyFiveCandidatesCanBeCaptured
import com.isl.leaseManagement.utils.Utilities.closeKeypad
import com.isl.leaseManagement.utils.Utilities.ellipTextSizeToSpecificLength
import com.isl.leaseManagement.utils.Utilities.keypadDoneClicked
import com.isl.leaseManagement.utils.Utilities.openKeypad
import infozech.itower.R
import infozech.itower.databinding.ActivitySelectExistingCandidateBinding
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SelectExistingCandidateActivity : BaseActivity() {

    private lateinit var binding: ActivitySelectExistingCandidateBinding
    private val api = ApiClient.request
    private val existingCandidateListAdapter = ExistingCandidateListAdapter(
        ArrayList(),
        this,
        object : ClickInterfaces.ExistingCandidateSelection {
            override fun candidateClicked(selectedCandidateData: ExistingCandidateListResponse.ExistingCandidateListResponseItem) {
                candidateSelected(selectedCandidateData)
            }
        })
    private val pageSize = 10
    private var pageNo = 1
    private var isLoading = false
    private val filterIntentCode = 100
    private val candidatesList: ArrayList<ExistingCandidateListResponse.ExistingCandidateListResponseItem> =
        arrayListOf()
    private var disposable: Disposable? = null
    private var alreadyCapturedCandidatesCount = 0
    private var districtIDSelected: Int? = 0
    private var districtNameSelected: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_existing_candidate)
        init()
    }

    private fun init() {
        getStartTaskData(MyApp.localTempVarStore.taskId)
        getCapturedCandidateCount()
        initializeCandidateAdapter()
        getExistingCandidates()
        addPaginationInCandidateRv()
        setClickListeners()
    }

    private fun getStartTaskData(taskId: Int) {
        disposable =
            commonDatabase.captureCandidateStartDao()
                .getBtsCaptureCandidateStartResponseByID(taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ startResponse ->
                    startResponse.reqDistrictName?.let {
                        districtNameSelected = it

                        binding.districtSelectedTv.text = ellipTextSizeToSpecificLength(it, 15)
                    }
                    startResponse.reqDistrictId?.let {
                        districtIDSelected = it
                    }
                }, { error ->
                })
    }

    private fun getCapturedCandidateCount() {
        getCapturedCandidateList { candidatesList ->
            candidatesList?.let {
                alreadyCapturedCandidatesCount = it.size
            }
        }
    }

    private fun getCapturedCandidateList(callback: (List<ExistingCandidateListResponse.ExistingCandidateListResponseItem>?) -> Unit) {
        disposable =
            commonDatabase.existingCandidateListResponseItemDao()
                .getAllCandidates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ candidatesList ->
                    callback(candidatesList)
                }, { error ->
                })
    }

    private fun candidateSelected(selectedCandidateData: ExistingCandidateListResponse.ExistingCandidateListResponseItem) {
        val exists = candidatesList.any { it.propertyId == selectedCandidateData.propertyId }
        candidatesList.removeAll { it.propertyId == selectedCandidateData.propertyId }
        if (!exists) {
            candidatesList.add(selectedCandidateData)
        }
        binding.candidateSelectedTvCount.text = candidatesList.size.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == filterIntentCode && resultCode == Activity.RESULT_OK) {
            val districtName =
                data?.getStringExtra(AppConstants.ActivityResultKeys.FilterCandidateActivity.districtSelectedName)
            val districtID =
                data?.getIntExtra(
                    AppConstants.ActivityResultKeys.FilterCandidateActivity.districtSelectedID,
                    0
                )
            binding.districtSelectedTv.text = ellipTextSizeToSpecificLength(districtName, 15)
            clearCandidateList()
            getExistingCandidates(districtId = districtID)

            districtIDSelected = districtID
            districtNameSelected = districtName

        }
    }

    private fun setClickListeners() {
        binding.searchIv.setOnClickListener {
            searchClicked()
        }
        binding.toolbarFilterIv.setOnClickListener {
            val intent = Intent(this, FilterCandidateActivity::class.java)
            intent.putExtra(
                AppConstants.ActivityResultKeys.FilterCandidateActivity.districtSelectedName,
                districtNameSelected
            )
            intent.putExtra(
                AppConstants.ActivityResultKeys.FilterCandidateActivity.districtSelectedID,
                districtIDSelected
            )
            startActivityForResult(
                intent,
                filterIntentCode
            )

        }
        binding.toolbarBackIv.setOnClickListener { backClicked() }
        binding.actionBtn.setOnClickListener {
            ActionButtonMethods.CaptureCandidateActions.showActionPopup(
                this
            )
        }
        binding.saveBtn.setOnClickListener { saveCandidatesListAndProceed(candidatesList) }
    }

    private fun saveCandidatesListAndProceed(candidates: List<ExistingCandidateListResponse.ExistingCandidateListResponseItem>) {
        if (candidates.size + alreadyCapturedCandidatesCount > 5) {
            showToastMessage(onlyFiveCandidatesCanBeCaptured)
            return
        }
        saveOrLinkCandidateToApi(candidates)
    }

    private fun saveOrLinkCandidateToApi(candidates: List<ExistingCandidateListResponse.ExistingCandidateListResponseItem>) {
        val requestID = MyApp.localTempVarStore.taskResponse?.requestId
        val selectedProperties: List<LinkExistingCandidateRequest.SelectedProperty?> = candidates
            .filter { it.propertyId != null }  // Ensure that propertyId is not null
            .map { candidate ->
                LinkExistingCandidateRequest.SelectedProperty(
                    propertyId = candidate.propertyId
                )
            }

        val requestBody = LinkExistingCandidateRequest(
            requestId = requestID,
            selectedProperties = selectedProperties
        )

        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""
        val observable: Observable<LinkExistingCandidateResponse> =
            api!!.linkExistingCandidate(
                tenantId = KotlinPrefkeeper.leaseManagementUserID!!,
                userId = lsmUserId,
                taskId = MyApp.localTempVarStore.taskId,
                body = requestBody
            )
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :
                Observer<LinkExistingCandidateResponse> {
                override fun onSubscribe(d: Disposable) {
                    showProgressBar()
                }

                override fun onNext(t: LinkExistingCandidateResponse) {
                    hideProgressBar()
                    t.data
                }

                override fun onError(e: Throwable) {
                    hideProgressBar()
                    showToastMessage(MessageConstants.ErrorMessages.unableToSaveExistingCandidate)
                }

                override fun onComplete() {
                }
            })

        //    saveCandidateToRoom(candidates)   // in success
    }

    private fun saveCandidateToRoom(candidates: List<ExistingCandidateListResponse.ExistingCandidateListResponseItem>) {
        disposable =
            commonDatabase.existingCandidateListResponseItemDao()
                .insertAll(candidates)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ rowIds ->
                    launchActivity(CapturedCandidateActivity::class.java)
                    finish()
                }, { error ->
                    // Handle error
                })
    }

    private fun backClicked() {
        if (binding.searchEt.visibility == View.VISIBLE) {
            binding.searchEt.visibility = View.GONE
            binding.searchIv.visibility = View.VISIBLE
            binding.searchEt.setText("")
            binding.searchEt.closeKeypad()
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        backClicked()
    }

    private fun searchClicked() {
        binding.searchEt.visibility = View.VISIBLE
        binding.searchIv.visibility = View.GONE
        binding.searchEt.requestFocus()
        binding.searchEt.openKeypad(this)
        binding.searchEt.keypadDoneClicked(triggerActionInterface = object :
            ClickInterfaces.TriggerActionInterface {
            override fun triggerAction() {
                clearCandidateList()
                getExistingCandidates(
                    searchString = binding.searchEt.text.toString()
                )
            }
        })
    }

    private fun clearCandidateList() {
        candidatesList.clear()
        binding.candidateSelectedTvCount.text = getString(R.string._0)
        existingCandidateListAdapter.removeAllData()
    }

    private fun initializeCandidateAdapter() {
        binding.candidatesListRv.layoutManager = LinearLayoutManager(this)
        binding.candidatesListRv.adapter = existingCandidateListAdapter
    }

    private fun addPaginationInCandidateRv() {
        binding.candidatesListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + pastVisibleItems) >= totalItemCount && dy > 0) {
                    isLoading = true
                    pageNo += 1
                    getExistingCandidates()
                }
            }
        })
    }

    private fun getExistingCandidates(districtId: Int? = null, searchString: String? = null) {
        val layoutManager = binding.candidatesListRv.layoutManager as LinearLayoutManager
        val currentPosition =
            layoutManager.findFirstVisibleItemPosition()      // Save the current scroll position

        val observable: Observable<ExistingCandidateListResponse> =
            api!!.getExistingCandidateList(
                tenantId = KotlinPrefkeeper.leaseManagementUserID!!,
                districtId = districtId,
                pageNo = pageNo,
                pageSize = pageSize,
                searchString = searchString
            )
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :
                Observer<ExistingCandidateListResponse> {
                override fun onSubscribe(d: Disposable) {
                    showProgressBar()
                }

                override fun onNext(t: ExistingCandidateListResponse) {
                    hideProgressBar()
                    getCapturedCandidateList { list ->
                        t.removeAll { newItem ->
                            list?.any { capturedListItem ->
                                capturedListItem._Id == newItem._Id
                            } == true
                        }
                        existingCandidateListAdapter.addMoreCandidates(t)
                        binding.candidatesListRv.post {
                            layoutManager.scrollToPositionWithOffset(currentPosition, 0)
                        }
                        isLoading = false
                    }
                }

                override fun onError(e: Throwable) {
                    hideProgressBar()
                    showToastMessage(MessageConstants.ErrorMessages.unableToGetExistingCandidatesList)
                }

                override fun onComplete() {
                }
            })
    }


    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
}