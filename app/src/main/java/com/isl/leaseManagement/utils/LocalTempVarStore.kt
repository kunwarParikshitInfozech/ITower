package com.isl.leaseManagement.utils

import com.isl.leaseManagement.dataClass.responses.StartTaskResponse
import com.isl.leaseManagement.dataClass.responses.TaskResponse

object LocalTempVarStore {
    var taskId: Int = 0
    var startTaskResponse: StartTaskResponse? = null
    var taskResponse: TaskResponse? = null
}