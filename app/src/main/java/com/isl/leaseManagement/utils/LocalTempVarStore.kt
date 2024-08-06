package com.isl.leaseManagement.utils

import com.isl.leaseManagement.dataClasses.responses.StartTaskResponse
import com.isl.leaseManagement.dataClasses.responses.TaskResponse

object LocalTempVarStore {
    var taskId: Int = 0
    var startTaskResponse: StartTaskResponse? = null
    var taskResponse: TaskResponse? = null
}