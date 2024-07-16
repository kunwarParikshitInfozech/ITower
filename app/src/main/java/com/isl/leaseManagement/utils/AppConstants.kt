package com.isl.leaseManagement.utils

object AppConstants {
    //   const val baseUrl = "https://ileasemvp.infozech.com/rest/mobileappservices/v1/"
    const val baseUrl = "https://ileasemvp-prod.infozech.com/rest/mobileappservices/v1/"

    object IntentKeys {
        const val taskDetailIntentExtra = "TaskDetailIntentExtra"
    }

    object ActivityResultKeys {
        object FilterActivity {
            const val task = "Task"
            const val slaStatus = "SlaStatus"
            const val priority = "Priority"
        }
    }

    object KeyWords {
        const val sadad = "SADAD"
        const val check = "Check"
        const val iban = "IBAN"
        const val leaseRentDocTagName = "Lease Rent VAT Document"
        const val sadadDocumentTagName = "SADAD Document"
        const val additionalDocumentTagName = "Others"
    }

    object PrefsName {
        const val isOnDuty = "isOnDuty"
        const val ADDITIONAL_DOCUMENTS_DOC_ID = "additionalDocumentsDocId"
        const val ADDITIONAL_DOCUMENTS_DOC_DATA_List = "AdditionalDocumentsDocDataList"
    }

}