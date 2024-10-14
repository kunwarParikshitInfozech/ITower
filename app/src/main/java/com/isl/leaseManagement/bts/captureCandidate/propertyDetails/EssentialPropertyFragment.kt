package com.isl.leaseManagement.bts.captureCandidate.propertyDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.lifecycle.lifecycleScope
import com.isl.itower.MyApp
import com.isl.leaseManagement.api.ApiClient
import com.isl.leaseManagement.base.BaseFragment
import com.isl.leaseManagement.bts.adapter.AreaType
import com.isl.leaseManagement.bts.adapter.SpinnerGenericAPILovAdapter
import com.isl.leaseManagement.bts.adapter.SpinnerGenericLocationAdapter
import com.isl.leaseManagement.bts.adapter.SpinnerGenericYesNoAdapter
import com.isl.leaseManagement.dataClasses.responses.CandidateDetailsAPIResponse
import com.isl.leaseManagement.dataClasses.responses.DropdownDataResponse
import com.isl.leaseManagement.dataClasses.responses.LocationsListResponse
import com.isl.leaseManagement.dataClasses.responses.UpdatePropertyResponse
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper
import com.isl.leaseManagement.utils.AppConstants
import com.isl.leaseManagement.utils.Utilities.getLocationListForUniqueRegions
import com.isl.leaseManagement.utils.Utilities.getLocationsListForCitiesInDistrict
import com.isl.leaseManagement.utils.Utilities.getLocationsListForDistrictsInRegion
import infozech.itower.databinding.FragmentEssentialPropertyBinding
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EssentialPropertyFragment : BaseFragment() {

    private lateinit var binding: FragmentEssentialPropertyBinding
    private var disposable: Disposable? = null
    private var currentCandidateDetailsResponse =
        CandidateDetailsAPIResponse(     // initially passing 0,will update this later and  will use this throughout
            0,
            null, null, null, null, null
        )
    private var autoGeneratedId = 0
    private val api = ApiClient.request

    private lateinit var areaTypeAdapter: SpinnerGenericAPILovAdapter
    private lateinit var leaseSpaceTypeAdapter: SpinnerGenericAPILovAdapter
    private lateinit var siteTypeAdapter: SpinnerGenericAPILovAdapter
    private lateinit var accessRoadAdapter: SpinnerGenericAPILovAdapter
    private lateinit var powerSourceTypeAdapter: SpinnerGenericAPILovAdapter

    private lateinit var regionAdapter: SpinnerGenericLocationAdapter
    private lateinit var districtAdapter: SpinnerGenericLocationAdapter
    private lateinit var cityAdapter: SpinnerGenericLocationAdapter

    private val completeDropdownList =
        KotlinPrefkeeper.dropdownsList
            ?: DropdownDataResponse() // empty to avoid null exception
    private val areaTypeList =
        completeDropdownList.getDropdownsListByParamType(AppConstants.DropDownParamTypes.areaType)
    private val leaseSpaceTypeList =
        completeDropdownList.getDropdownsListByParamType(AppConstants.DropDownParamTypes.leasedSpaceType)
    private val siteTypeList =
        completeDropdownList.getDropdownsListByParamType(AppConstants.DropDownParamTypes.siteType)
    private val accessRoadList =
        completeDropdownList.getDropdownsListByParamType(AppConstants.DropDownParamTypes.accessRoad)
    private val powerSourceTypeList =
        completeDropdownList.getDropdownsListByParamType(AppConstants.DropDownParamTypes.powerSourceType)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEssentialPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        autoGeneratedId =
            (arguments?.getString(AppConstants.IntentKeys.autoGeneratedId)?.toInt()) ?: 0
        attachValuesToAPISpinners()
        attachValuesToYesNoSpinner()
        if (autoGeneratedId != 0) {
            getCandidateData()
        }
        setClickListeners()
    }

    private fun attachValuesToYesNoSpinner() {
        createAdapterAndAttachToYesNoSpinner(binding.pentHouseAvailableSpinner)
        createAdapterAndAttachToYesNoSpinner(binding.momraRegulationsSpinner)
        createAdapterAndAttachToYesNoSpinner(binding.ownerApprovalToInstallSubStationSpinner)
        createAdapterAndAttachToYesNoSpinner(binding.secoAccessToRoadSpinner)
        createAdapterAndAttachToYesNoSpinner(binding.availablePowerSourceOwnerSpinner)
        createAdapterAndAttachToYesNoSpinner(binding.obstructionsExistSpinner)
        createAdapterAndAttachToYesNoSpinner(binding.landlordApprovalSpinner)
        createAdapterAndAttachToYesNoSpinner(binding.landDeedAvailableSpinner)
    }

    private fun createAdapterAndAttachToYesNoSpinner(spinner: Spinner) {
        val adapter = SpinnerGenericYesNoAdapter(
            context = baseActivity
        )
        adapter.attachSpinner(spinner)
    }

    private fun attachValuesToAPISpinners() {
        areaTypeAdapter = createAPISpinnerAdapter(areaTypeList)
        leaseSpaceTypeAdapter = createAPISpinnerAdapter(leaseSpaceTypeList)
        siteTypeAdapter = createAPISpinnerAdapter(siteTypeList)
        accessRoadAdapter = createAPISpinnerAdapter(accessRoadList)
        powerSourceTypeAdapter = createAPISpinnerAdapter(powerSourceTypeList)

        areaTypeAdapter.attachSpinner(binding.areaTypeSpinner)
        leaseSpaceTypeAdapter.attachSpinner(binding.leaseSpaceTypeSpinner)
        siteTypeAdapter.attachSpinner(binding.siteTypeSpinner)
        accessRoadAdapter.attachSpinner(binding.accessRoadRequiredSpinner)
        powerSourceTypeAdapter.attachSpinner(binding.powerSourceTypeSpinner)

    }

    private fun createAPISpinnerAdapter(dropdownList: DropdownDataResponse): SpinnerGenericAPILovAdapter {
        return SpinnerGenericAPILovAdapter(
            context = baseActivity,
            items = dropdownList
        )
    }

    private fun setClickListeners() {
        binding.saveAsDraftBtn.setOnClickListener { checkAutoGenUniqueIdAndSaveToRoom(false) }
        binding.saveToApiAndDraft.setOnClickListener { checkAutoGenUniqueIdAndSaveToRoom(true) }
    }

    private fun checkAutoGenUniqueIdAndSaveToRoom(saveToApi: Boolean) {
        if (autoGeneratedId == 0) {  // new candidate so creating new Unique ID, unique in existing captured candidate table as well
            val existingCapturedCandidateDao =
                baseActivity.commonDatabase.existingCandidateListResponseItemDao()
            disposable = existingCapturedCandidateDao.getMaxId().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    { maxId ->
                        autoGeneratedId =
                            (maxId
                                ?: 0) + 1  // Increment the max ID, or start from 1 if no IDs exist
                        updateCandidateForSavingToRoom(saveToApi)   // saving with new ID
                    }, { error ->
                        //       saveCandidateToRoom()
                    }
                )
        } else {
            updateCandidateForSavingToRoom(saveToApi)  // saving with pre existing Unique ID
        }
    }

    private fun updateCandidateForSavingToRoom(saveToApi: Boolean) {
        currentCandidateDetailsResponse.autoGeneratedId = autoGeneratedId
        if (currentCandidateDetailsResponse.propertyDetail == null) {  //if it is blank that is not saved yet, creating new
            currentCandidateDetailsResponse.propertyDetail =
                CandidateDetailsAPIResponse.PropertyDetails()
        }
        currentCandidateDetailsResponse.propertyDetail!!.let {  //now, can not be null
                property ->

            property.landlordPrice = binding.landlordPriceEt.text.toString()
            property.siteAddress = binding.siteAddressET.text.toString()
            property.siteDistrictLocal = binding.siteDistrictArabicValue.text.toString()
            property.siteCityLocal = binding.siteCityArabicValue.text.toString()
            property.siteStreet = binding.siteStreetEt.text.toString()
            property.siteStreetInArabic = binding.siteStreetArabicEt.text.toString()
            property.distanceFromRoadInMeter = binding.distanceFromRoadEt.text.toString()
            property.siteNeighbourhood = binding.siteNeighbourhoodEt.text.toString()
            property.siteNeighbourhoodInArabic = binding.siteNeighbourhoodArabicEt.text.toString()

            property.latitude = binding.latitudeEt.text.toString()
            property.longitude = binding.longitudeEt.text.toString()
            property.distanceFromNominalInMeter = binding.distanceFromNominalValue.text.toString()

            property.approxBuildingHeightWithoutParapetInMeter =
                binding.approxBuildingHeightWithoutParaperET.text.toString()

//            property.ownerApprovalToInstallSubStation =
//                binding.ownerApprovalToInstallSubstationET.text.toString()
            property.powerSourceDistanceFromSubstation =
                binding.powerSourceDistanceSubstationET.text.toString()
            property.powerSourceDistanceFromRoad = binding.powerSourceDistanceRoadET.text.toString()
            //         property.scecoAccessToRoad = binding.scecoAccessToRoadET.text.toString()
            property.siteArea = binding.siteAreaLengthWidthET.text.toString()
            property.leaseAreaInSqMtr = binding.leaseAreaET.text.toString()
            property.nearestElectricityPointDistanceInMeter =
                binding.nearestElectricityPointDistanceET.text.toString()
            property.distanceFromNearestOperatorTower =
                binding.distanceFromNearestOperatorET.text.toString()
            property.ownerDeedAndIdDoc = binding.potentialPropertyStatusET.text.toString()


            //getting and saving from location spinners
            val selectedRegion = binding.regionSpinner.selectedItem
            if (selectedRegion is LocationsListResponse.LocationsListResponseItem) {
                val regionName = selectedRegion.regionName
                property.region = regionName
            }

            // For district
            val selectedDistrict = binding.districtSpinner.selectedItem
            if (selectedDistrict is LocationsListResponse.LocationsListResponseItem) {
                val districtName = selectedDistrict.districtName
                property.siteDistrict =
                    districtName
            }

            // For city
            val selectedCity = binding.citySpinner.selectedItem
            if (selectedCity is LocationsListResponse.LocationsListResponseItem) {
                val cityName = selectedCity.cityName
                property.siteCity = cityName
            }

            //getting data from API spinners
            property.areaType = areaTypeAdapter.getSelectedItem()?.paramValue
            property.leasedSpaceType = leaseSpaceTypeAdapter.getSelectedItem()?.paramValue
            property.siteType = siteTypeAdapter.getSelectedItem()?.paramValue
            property.accessRoad = accessRoadAdapter.getSelectedItem()?.paramValue
            property.powerSourceType = powerSourceTypeAdapter.getSelectedItem()?.paramValue

            //getting from yes/no spinners

            property.pentHouseAvailable = binding.pentHouseAvailableSpinner.selectedItem.toString()
            property.candidateCompliedWithMOMRARegulations =
                binding.momraRegulationsSpinner.selectedItem.toString()

            property.ownerApprovalToInstallSubStation =
                binding.ownerApprovalToInstallSubStationSpinner.selectedItem.toString()
            property.scecoAccessToRoad = binding.secoAccessToRoadSpinner.selectedItem.toString()
            property.availablePowerSourceFromOwner =
                binding.availablePowerSourceOwnerSpinner.selectedItem.toString()

            property.obstructionExist = binding.obstructionsExistSpinner.selectedItem.toString()
            property.landlordApproval = binding.landlordApprovalSpinner.selectedItem.toString()
            property.landDeed = binding.landDeedAvailableSpinner.selectedItem.toString()

        }

        saveCandidateDetailsToRoom(currentCandidateDetailsResponse, saveToApi)
    }

    private fun saveCandidateDetailsToRoom(
        candidateDetailsAPIResponse: CandidateDetailsAPIResponse,
        saveToApi: Boolean
    ) {
        val candidateDetailsDao = baseActivity.commonDatabase.candidateDetailsDao()
        disposable = candidateDetailsDao.insertCandidateDetails(candidateDetailsAPIResponse)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (saveToApi) {
                    saveCandidateToApi(currentCandidateDetailsResponse)
                } else {
                    baseActivity.showToastMessage("Data saved to phone!")
                }
            }, { error ->
                baseActivity.showToastMessage("Unable to save to room!")
            })
    }

    private fun saveCandidateToApi(currentCandidateDetailsResponse: CandidateDetailsAPIResponse) {
        currentCandidateDetailsResponse.propertyDetail ?: return
        val lsmUserId = KotlinPrefkeeper.lsmUserId ?: ""  // was used earlier
        val observable: Observable<UpdatePropertyResponse> =
            api!!.updateEssentialProperties(
                tenantId = KotlinPrefkeeper.leaseManagementUserID!!,
                taskId = MyApp.localTempVarStore.taskId,
                body = currentCandidateDetailsResponse.propertyDetail!!
            )
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :
                Observer<UpdatePropertyResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(linkExistingCandidateResponse: UpdatePropertyResponse) {
                    baseActivity.showToastMessage("Updated to api")
                }

                override fun onError(e: Throwable) {
                    baseActivity.showToastMessage("unable to update to api")
                }

                override fun onComplete() {
                }
            })
    }

    private fun getCandidateData(propertyId: String? = null) {
        val candidateDetailsDao = baseActivity.commonDatabase.candidateDetailsDao()
        disposable = candidateDetailsDao.getCandidateDetailsByAutoGeneratedId(autoGeneratedId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ candidateDetailsResponse ->
                if (candidateDetailsResponse.propertyDetail?._id != null) { // need to get candidate details
                    currentCandidateDetailsResponse =
                        candidateDetailsResponse
                    fillCandidateDetails(candidateDetailsResponse)
                }
            }, { error -> // error as details not present so, not filling details
            })

    }

    private fun fillCandidateDetails(candidateDetailsResponse: CandidateDetailsAPIResponse) {
        val uniqueRegions: List<LocationsListResponse.LocationsListResponseItem> =
            getLocationListForUniqueRegions() ?: return

        var regionSelected: String? = null
        var districtSelected: String? = null
        var citySelected: String? = null

        var districtsForSelectedRegion: List<LocationsListResponse.LocationsListResponseItem>? =
            null
        var cityForSelectedDistrict: List<LocationsListResponse.LocationsListResponseItem>? =
            null

        candidateDetailsResponse.propertyDetail?.let { property ->
            binding.propertyIdET.setText(property.propertyId ?: "")
            binding.landlordPriceEt.setText(property.landlordPrice ?: "")
            binding.siteAddressET.setText(property.siteAddress ?: "")
            binding.siteDistrictArabicValue.setText(property.siteDistrictLocal ?: "")
            binding.siteCityArabicValue.setText(property.siteCityLocal ?: "")
            binding.siteStreetEt.setText(property.siteStreet ?: "")
            binding.siteStreetArabicEt.setText(property.siteStreetInArabic ?: "")
            binding.distanceFromRoadEt.setText(property.distanceFromRoadInMeter ?: "")
            binding.siteNeighbourhoodEt.setText(property.siteNeighbourhood ?: "")
            binding.siteNeighbourhoodArabicEt.setText(property.siteNeighbourhoodInArabic ?: "")

            binding.latitudeEt.setText(property.latitude ?: "")
            binding.longitudeEt.setText(property.longitude ?: "")
            binding.distanceFromNominalValue.setText(
                property.distanceFromNominalInMeter ?: ""
            )

            binding.approxBuildingHeightWithoutParaperET.setText(
                property.approxBuildingHeightWithoutParapetInMeter ?: ""
            )

            //         binding.ownerApprovalToInstallSubstationET.setText(
//                property.ownerApprovalToInstallSubStation ?: ""
//            )
            binding.powerSourceDistanceSubstationET.setText(
                property.powerSourceDistanceFromSubstation ?: ""
            )
            binding.powerSourceDistanceRoadET.setText(
                property.powerSourceDistanceFromRoad ?: ""
            )
            //      binding.scecoAccessToRoadET.setText(property.scecoAccessToRoad ?: "")
            binding.siteAreaLengthWidthET.setText(property.siteArea ?: "")
            binding.leaseAreaET.setText(property.leaseAreaInSqMtr ?: "")
            binding.nearestElectricityPointDistanceET.setText(
                property.nearestElectricityPointDistanceInMeter ?: ""
            )
            binding.distanceFromNearestOperatorET.setText(
                property.distanceFromNearestOperatorTower ?: ""
            )
            binding.potentialPropertyStatusET.setText(property.ownerDeedAndIdDoc ?: "")


            //filling API spinners
            selectPositionForApiSpinners(binding.areaTypeSpinner, property.areaType, areaTypeList)
            selectPositionForApiSpinners(
                binding.leaseSpaceTypeSpinner,
                property.leasedSpaceType,
                leaseSpaceTypeList
            )
            selectPositionForApiSpinners(binding.siteTypeSpinner, property.siteType, siteTypeList)
            selectPositionForApiSpinners(
                binding.accessRoadRequiredSpinner,
                property.accessRoad,
                accessRoadList
            )
            selectPositionForApiSpinners(
                binding.powerSourceTypeSpinner,
                property.powerSourceType,
                powerSourceTypeList
            )


            // filling yes no spinners
            selectPositionForYesNoSpinners(
                binding.pentHouseAvailableSpinner,
                property.pentHouseAvailable
            )
            selectPositionForYesNoSpinners(
                binding.momraRegulationsSpinner,
                property.candidateCompliedWithMOMRARegulations
            )
            selectPositionForYesNoSpinners(
                binding.ownerApprovalToInstallSubStationSpinner,
                property.ownerApprovalToInstallSubStation
            )
            selectPositionForYesNoSpinners(
                binding.secoAccessToRoadSpinner,
                property.scecoAccessToRoad
            )
            selectPositionForYesNoSpinners(
                binding.availablePowerSourceOwnerSpinner,
                property.availablePowerSourceFromOwner
            )
            selectPositionForYesNoSpinners(
                binding.obstructionsExistSpinner,
                property.obstructionExist
            )
            selectPositionForYesNoSpinners(
                binding.landlordApprovalSpinner,
                property.landlordApproval
            )
            selectPositionForYesNoSpinners(
                binding.landDeedAvailableSpinner,
                property.landDeed
            )

            //for location selection spinners getting lists for dist based on region and city based on district
            districtsForSelectedRegion = getLocationsListForDistrictsInRegion(property.region)
            cityForSelectedDistrict = getLocationsListForCitiesInDistrict(property.siteDistrict)
            regionSelected = property.region
            districtSelected = property.siteDistrict
            citySelected = property.siteCity

        }

        attachValuesToLocationsSpinners(
            uniqueRegions,
            districtsForSelectedRegion,
            cityForSelectedDistrict, regionSelected, districtSelected, citySelected
        )
    }

    private fun attachValuesToLocationsSpinners(
        uniqueRegions: List<LocationsListResponse.LocationsListResponseItem>?,
        districtsForSelectedRegion: List<LocationsListResponse.LocationsListResponseItem>?,
        cityForSelectedDistrict: List<LocationsListResponse.LocationsListResponseItem>?,
        regionSelected: String?,
        districtSelected: String?, citySelected: String?
    ) {
        regionAdapter = createRegionSpinnerAdapter(uniqueRegions ?: listOf())
        regionAdapter.attachSpinner(binding.regionSpinner)
        val positionRegion =
            uniqueRegions?.indexOfFirst { it.regionName == regionSelected } ?: -1
        if (positionRegion != -1) {
            binding.regionSpinner.setSelection(positionRegion)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            delay(200)
            districtAdapter = createDistrictSpinnerAdapter(districtsForSelectedRegion ?: listOf())
            districtAdapter.attachSpinner(binding.districtSpinner)

            delay(400)
            val position =
                districtsForSelectedRegion?.indexOfFirst { it.districtName == districtSelected }
                    ?: -1
            if (position != -1) {
                binding.districtSpinner.setSelection(position)
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            delay(600)
            cityAdapter = createCitySpinnerAdapter(cityForSelectedDistrict ?: listOf())
            cityAdapter.attachSpinner(binding.citySpinner)

            delay(800)
            val position =
                cityForSelectedDistrict?.indexOfFirst { it.cityName == citySelected } ?: -1
            if (position != -1) {
                binding.citySpinner.setSelection(position)
            }
        }
    }

    private fun createRegionSpinnerAdapter(
        locationList: List<LocationsListResponse.LocationsListResponseItem>,
    ): SpinnerGenericLocationAdapter {
        return SpinnerGenericLocationAdapter(
            context = baseActivity,
            items = locationList,
            object : SpinnerGenericLocationAdapter.OnItemSelectedListener {
                override fun onItemSelected(
                    item: LocationsListResponse.LocationsListResponseItem,
                    position: Int
                ) {
                    if (::districtAdapter.isInitialized) {
                        item.regionName ?: return
                        val districtLocationList =
                            getLocationsListForDistrictsInRegion(item.regionName) ?: return
                        districtAdapter.updateData(districtLocationList)
                    }
                }
            },
            AreaType.REGION
        )
    }

    private fun createDistrictSpinnerAdapter(
        locationList: List<LocationsListResponse.LocationsListResponseItem>,
    ): SpinnerGenericLocationAdapter {
        return SpinnerGenericLocationAdapter(
            context = baseActivity,
            items = locationList,
            object : SpinnerGenericLocationAdapter.OnItemSelectedListener {
                override fun onItemSelected(
                    item: LocationsListResponse.LocationsListResponseItem,
                    position: Int
                ) {
                    if (::cityAdapter.isInitialized) {
                        item.districtName ?: return
                        val cityLocationList =
                            getLocationsListForCitiesInDistrict(item.districtName) ?: return
                        cityAdapter.updateData(cityLocationList)
                    }
                }
            },
            AreaType.DISTRICT
        )
    }

    private fun createCitySpinnerAdapter(locationList: List<LocationsListResponse.LocationsListResponseItem>): SpinnerGenericLocationAdapter {
        return SpinnerGenericLocationAdapter(
            context = baseActivity,
            items = locationList,
            object : SpinnerGenericLocationAdapter.OnItemSelectedListener {
                override fun onItemSelected(
                    item: LocationsListResponse.LocationsListResponseItem,
                    position: Int
                ) {
                    //       item.cityName?.let { baseActivity.showToastMessage(it) }
                }

            },
            AreaType.CITY
        )
    }


    private fun selectPositionForYesNoSpinners(
        spinner: Spinner,
        value: String?
    ) {
        value ?: return
        val predefinedValues = listOf("Choose an option", "Yes", "No", "Unavailable")
        val position = predefinedValues.indexOf(value)
        if (position != -1) {
            spinner.setSelection(position)
        }
    }

    private fun selectPositionForApiSpinners(
        spinner: Spinner,
        paramValue: String?,
        dropdownList: List<DropdownDataResponse.DropdownDataResponseItem>
    ) {
        paramValue ?: return
        val position = dropdownList.indexOfFirst { it.paramValue == paramValue }
        if (position != -1) {
            spinner.setSelection(position)
        }
    }

}