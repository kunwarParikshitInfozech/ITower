package com.isl.leaseManagement.utils

import com.isl.leaseManagement.dataClass.responses.TaskResponse

object ClickInterfaces {
    interface MyTasks {
        fun myTaskClicked(taskResponse: TaskResponse)
    }

}