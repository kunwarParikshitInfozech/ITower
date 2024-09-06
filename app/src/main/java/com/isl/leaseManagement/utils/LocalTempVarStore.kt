package com.isl.leaseManagement.utils

import com.isl.leaseManagement.dataClasses.responses.BaladiyaNamesListResponse
import com.isl.leaseManagement.dataClasses.responses.PaymentStartTaskResponse
import com.isl.leaseManagement.dataClasses.responses.TaskResponse

object LocalTempVarStore {   //this class is used for storing local instance which are used in next parts of the app, which don't need to be saved in phone to reduce processing
    var taskId: Int = 0
    var processId = 0
    var paymentStartTaskResponse: PaymentStartTaskResponse? = null
    var taskResponse: TaskResponse? = null
    var requestRemarkFieldData: String? = null   //only with baladiya, not payment
    var baladiyaNameList: BaladiyaNamesListResponse? = null
    var isStartCalledFromRoom: Boolean = false
}