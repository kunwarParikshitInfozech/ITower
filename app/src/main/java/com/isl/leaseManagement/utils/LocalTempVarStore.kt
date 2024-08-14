package com.isl.leaseManagement.utils

import com.isl.leaseManagement.dataClasses.responses.BaladiyaNamesListResponse
import com.isl.leaseManagement.dataClasses.responses.PaymentStartTaskResponse
import com.isl.leaseManagement.dataClasses.responses.TaskResponse

object LocalTempVarStore {
    var taskId: Int = 0
    var paymentStartTaskResponse: PaymentStartTaskResponse? = null
    var taskResponse: TaskResponse? = null
    var requestRemarkFieldData: TaskResponse? = null
    var baladiyaNameList: BaladiyaNamesListResponse? = null
}