package com.isl.leaseManagement.dataClasses.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity
data class CandidateDetailsAPIResponse(
    @PrimaryKey(autoGenerate = true) var autoGeneratedId: Int = 0, //for room unique identification,not from api, used in mapping property details, landlord details etc.. no need of property id for mapping

    var candidateId: Int?,

    @TypeConverters(AccountDetailConverter::class)
    val accountDetails: List<AccountDetail?>?,

    @TypeConverters(DelegateDetailConverter::class)
    val delegateDetail: List<DelegateDetail?>?,

    @TypeConverters(LandlordDetailConverter::class)
    val landlordDetail: LandlordDetail?,

    @TypeConverters(PropertyDetailsConverter::class)
    var propertyDetail: PropertyDetails?
) {
    @Entity
    data class AccountDetail(
        val _Id: Int?,
        val accountHolder: String?,
        val accountId: String?,
        val accountNumber: String?,
        val bank: String?,
        val bankBranch: String?,
        val currencyCode: String?,

        @TypeConverters(DocumentConverter::class)
        val documents: List<Document?>?,

        val paymentMethod: String?
    ) {
        @Entity
        data class Document(
            val docId: Int?,
            val fileName: String?,
            val tagName: String?
        )
    }

    @Entity
    data class DelegateDetail(
        val _Id: Int?,
        val city: String?,
        val commercialRegistrationNo: String?,
        val country: String?,
        val delegateId: String?,
        val delegateName: String?,
        val delegateType: String?,
        val district: String?,

        @TypeConverters(DocumentConverter::class)
        val documents: List<Document?>?,

        val expireIn6Months: String?,
        val expiryDate: String?,
        val isVATApplicable: String?,
        val legalSignatureRights: String?,
        val nationalAddress: String?,
        val nationalIdNo: String?,
        val paymentCollectionRights: String?,
        val realEstateRentRights: String?,
        val realEstateRights: String?,
        val region: String?,
        val roles: String?,
        val startDate: String?,
        val taxOrganisationType: String?,
        val telephoneNo: String?
    ) {
        @Entity
        data class Document(
            val docId: Int?,
            val fileName: String?,
            val tagName: String?
        )
    }

    @Entity
    data class LandlordDetail(
        val _Id: Int?,
        val commercialRegistrationNo: String?,
        val countryCode: String?,

        @TypeConverters(DocumentConverter::class)
        val documents: List<Document?>?,

        val eligibleForSite: String?,
        val governmentType: String?,
        val ineligiblityJustification: String?,
        val investerFlag: String?,
        val isVATApplicable: String?,
        val landlordCity: String?,
        val landlordCityLocal: String?,
        val landlordCountry: String?,
        val landlordDistrict: String?,
        val landlordDistrictLocal: String?,
        val landlordId: String?,
        val landlordName: String?,
        val landlordNameLocal: String?,
        val landlordRegion: String?,
        val landlordRegionLocal: String?,
        val landlordType: String?,
        val nationalIdNo: String?,
        val nationalPOBoxNo: Int?,
        val taxOrganizationType: String?,
        val vatPercentage: String?
    ) {
        @Entity
        data class Document(
            val docId: Int?,
            val fileName: String?,
            val tagName: String?
        )
    }

    @Entity
    data class PropertyDetails(
        var _id: String? = null,
        var propertyId: String? = null,
        var accessRoad: String? = null,
        var ownerApprovalToInstallSubStation: String? = null,
        var approxBuildingHeightWithoutParapetInMeter: Double? = 0.0,
        var approxPentHouseHeight: String? = null,
        var areaType: String? = null,
        var civilRanking: String? = null,
        var candidateCompliedWithMOMRARegulations: String? = null,
        var distanceFromRoadInMeter: Double? = 0.0,

        @TypeConverters(DocumentConverter::class)
        var documents: List<Document?>? = null,

        var nearestElectricityPointDistanceInMeter: Double? = 0.0,
        var ownerDeedAndIdDoc: String? = null,
        var landlordApproval: String? = null,
        var landlordPrice: String? = null,
        var latitude: String? = null,
        var leaseAreaInSqMtr: Double? = 0.0,
        var leasedSpaceType: String? = null,
        var longitude: String? = null,
        var distanceFromNearestOperatorTower: Double? = 0.0,
        var distanceFromNominalInMeter: Double? = 0.0,
        var obstructionExist: String? = null,
        var pentHouseAvailable: String? = null,
        var powerSourceDistanceFromRoad: Double? = 0.0,
        var powerSourceDistanceFromSubstation: Double? = 0.0,
        var availablePowerSourceFromOwner: String? = null,
        var powerSourceType: String? = null,
        var scecoAccessToRoad: String? = null,
        var siteAddress: String? = null,
        var siteArea: String? = null,
        var siteCity: String? = null,
        var siteCityLocal: String? = null,
        var siteDistrict: String? = null,
        var siteDistrictLocal: String? = null,
        var siteNeighbourhood: String? = null,
        var siteNeighbourhoodInArabic: String? = null,
        var region: String? = null,
        var siteRegionLocal: String? = null,
        var siteStreet: String? = null,
        var siteStreetInArabic: String? = null,
        var siteType: String? = null,
        var substationInstallApproval: String? = null,
        var potentialPropertyStatus: String? = null
    ) {
        @Entity
        data class Document(
            val docId: Int?,
            val fileName: String?,
            val tagName: String?
        )
    }

    // Converters for List of AccountDetail, DelegateDetail, and their Documents
    class AccountDetailConverter {
        @TypeConverter
        fun fromAccountDetailList(accountDetail: List<AccountDetail?>?): String {
            return Gson().toJson(accountDetail)
        }

        @TypeConverter
        fun toAccountDetailList(data: String?): List<AccountDetail?>? {
            val type = object : TypeToken<List<AccountDetail?>>() {}.type
            return Gson().fromJson(data, type)
        }
    }

    class DelegateDetailConverter {
        @TypeConverter
        fun fromDelegateDetailList(delegateDetail: List<DelegateDetail?>?): String {
            return Gson().toJson(delegateDetail)
        }

        @TypeConverter
        fun toDelegateDetailList(data: String?): List<DelegateDetail?>? {
            val type = object : TypeToken<List<DelegateDetail?>>() {}.type
            return Gson().fromJson(data, type)
        }
    }

    class LandlordDetailConverter {
        @TypeConverter
        fun fromLandlordDetail(landlordDetail: LandlordDetail?): String {
            return Gson().toJson(landlordDetail)
        }

        @TypeConverter
        fun toLandlordDetail(data: String?): LandlordDetail? {
            val type = object : TypeToken<LandlordDetail>() {}.type
            return Gson().fromJson(data, type)
        }
    }

    class PropertyDetailsConverter {
        @TypeConverter
        fun fromPropertyDetails(propertyDetails: PropertyDetails?): String {
            return Gson().toJson(propertyDetails)
        }

        @TypeConverter
        fun toPropertyDetails(data: String?): PropertyDetails? {
            val type = object : TypeToken<PropertyDetails>() {}.type
            return Gson().fromJson(data, type)
        }
    }

    class DocumentConverter {
        @TypeConverter
        fun fromDocumentList(document: List<AccountDetail.Document?>?): String {
            return Gson().toJson(document)
        }

        @TypeConverter
        fun toDocumentList(data: String?): List<AccountDetail.Document?>? {
            val type = object : TypeToken<List<AccountDetail.Document?>>() {}.type
            return Gson().fromJson(data, type)
        }
    }
}
