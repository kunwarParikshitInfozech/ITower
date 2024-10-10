package com.isl.leaseManagement.bts.captureCandidate.requestDetailsBts

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.isl.itower.MyApp
import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClasses.responses.BTSRequestDetailsResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.ActionButtonMethods
import com.isl.leaseManagement.utils.Utilities
import infozech.itower.R
import infozech.itower.databinding.ActivityRequestDetailsBtsBinding
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class BtsRequestDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityRequestDetailsBtsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_details_bts)
        init()
    }

    private fun init() {
        getRequestDetailsData()
        setClickListeners()
    }

    private fun getRequestDetailsData() {
        MyApp.localTempVarStore?.let { tempVarStorage ->
            tempVarStorage.taskResponse?.let { taskResponse ->
                taskResponse.requestId?.let {
                    callRequestDetails(it)
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener { finish() }
        binding.actionBtn.setOnClickListener {
            ActionButtonMethods.Actions.showActionPopup(
                this, ActionButtonMethods.ActionOpeningProcess.BtsCaptureCandidate
            )
        }
    }


    private fun callRequestDetails(requestId: String) {
        val api = ApiClient.request
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""
        val observable: Observable<BTSRequestDetailsResponse> =
            api!!.getRequestDetailsBts(
                leasemanagementId = KotlinPrefkeeper.leaseManagementUserID!!,
                userId = lsmUserId,
                requestId = requestId
            )
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BTSRequestDetailsResponse> {
                override fun onSubscribe(d: Disposable) {
                    showProgressBar()
                }

                override fun onNext(t: BTSRequestDetailsResponse) {
                    fillRequestDetailsResponse(t)
                    hideProgressBar()
                }

                override fun onError(e: Throwable) {
                    hideProgressBar()
                }

                override fun onComplete() {
                }
            })
    }

    private fun fillRequestDetailsResponse(response: BTSRequestDetailsResponse) {
        binding.regionEt.text = response.region ?: ""
        binding.siteDistrictEt.text = response.district ?: ""
        binding.siteCityEt.text = response.city ?: ""
        binding.siteDistrictArabicEt.text = response.districtArabic ?: ""
        binding.siteCityArabicEt.text = response.cityArabic ?: ""
        binding.geoTypeClusterEt.text = response.geoTypeLocationType ?: ""
        binding.longitudeNominalPointEt.text = response.longitudeOfNominalPoint ?: ""
        binding.latitudeNominalPointEt.text = response.latitudeOfNominalPoint ?: ""
        binding.searchRingRadiusEt.text = response.searchRingRadius ?: ""
        binding.projectNameEt.text = response.projectName ?: ""
        binding.customerSiteIdEt.text = response.customerSiteId ?: ""
        binding.bssRequestNumberEt.text = response.bssRequestNumber ?: ""
        binding.requestingCustomerEt.text = response.requestingCustomer ?: ""
        binding.iProjectRequestIdEt.text = response.iprojectRequestId ?: ""
        binding.isItVipRequestEt.text = response.vipRequest ?: ""
        binding.customerCommercialAgreementEt.text = response.commercialAgreementNumber ?: ""
        binding.expectedCoLocationEt.text = response.expectedNumberOfColocationOnSite ?: ""
        binding.dpProposedTowerHeightEt.text = response.dpProposedTowerHeightForGfInMtr ?: ""
        binding.potentialCustomerEt.text = response.potentialCustomer ?: ""
        binding.expectedPackagesEt.text = response.expectedPackages ?: ""
        binding.forecastDateColocationEt.text = response.forecastDateForColocation ?: ""
        response.forecastDateForColocation?.let {
            binding.forecastDateColocationEt.text = Utilities.getDateFromISO8601(it)
        }
        binding.requestTypeEt.text = response.requestType ?: ""
        binding.versionEt.text = response.version ?: ""
    }


    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
}