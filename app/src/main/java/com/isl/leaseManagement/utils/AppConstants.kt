package com.isl.leaseManagement.utils

object AppConstants {
    //   const val baseUrl = "https://ileasemvp.infozech.com/rest/mobileappservices/v1/"
    const val baseUrl = "https://ileasemvp-prod.infozech.com/rest/mobileappservices/v1/"
    //const val baseUrl = "https://ilease-dev.tawal.com.sa/rest/mobileappservices/v1/"
    //  const val baseUrl = "https://ilease-qa.tawal.com.sa/rest/mobileappservices/v1/"

    object IntentKeys {
        const val taskDetailIntentExtra = "TaskDetailIntentExtra"
        const val isStartCalledFromRoom = "IsStartCalledFromRoom"
    }

    object ActivityResultKeys {
        object FilterActivity {
            const val task = "Task"
            const val slaStatus = "SlaStatus"
            const val priority = "Priority"
        }

        object UploadDocumentFragmentIntent {
            const val dataClassToJson = "DataClassToJson"
        }
    }

    object PaymentType {
        const val paymentTypeSadad = "SADAD"
        const val paymentTypeCheck = "Check"
        const val paymentTypeIban = "IBAN"

    }

    object DocsTagNames {
        const val leaseRentDocTagName = "Lease Rent VAT Document"
        const val sadadDocumentTagName = "SADAD Document"
        const val additionalDocumentTagName = "Others"
        const val fieldWorkProvideEvidence = "ProvideEvidenceFW"
        const val fieldWorkSadadDocument = "SADADDocumentFW"
        const val fieldWorkBaladiyaCertificate = "BaladiyaCertificate"
    }

    object DocTypeNames {
        const val provideEvidence = "ProvideEvidence"
        const val sadadDocument = "SADAD Document"
    }

    object TaskFlags {
        const val taskBaladiyaRequest = "BaladiyaRequest"
        const val taskBaladiyaCheckList = "BaladiyaRequestChecklist"
        const val taskBaladiyaUploadCertificate = "UploadBaladiyaCertificate"
    }

    object DropDownTypes {
        const val baladiyaNameTypeDropDown = "BaladiyaNameTypeDropDown"
    }

    object PrefsName {
        const val isOnDuty = "isOnDuty"
        const val lsmUserId = "lsmUserId"
        const val deviceUUID = "deviceUUID"
    }

    object ProcessIds {
        const val paymentProcess = 3
        const val baladiyaFieldWork = 2
    }

}