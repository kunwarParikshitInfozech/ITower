package com.isl.leaseManagement.room.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_response_table")
public class TaskResponsePOJO {

    @PrimaryKey
    private final int taskId;

    private final String requestId;
    private final String siteId;
    private final String customerSiteId;
    private final String taskName;
    private final String taskStatus;
    private final String requestPriority;
    private final String forecastStartDate;
    private final String forecastEndDate;
    private final String actualStartDate;
    private final Integer slaDuration; // Use Integer for nullable int
    private final String slaUnit;
    private final String processName;
    private final Integer processId; // Use Integer for nullable int
    private final String requestStatus;
    private final String slaStatus;
    private final String requester;
    private final String region;
    private final String district;
    private final String city;

    public TaskResponsePOJO(int taskId, String requestId, String siteId, String customerSiteId, String taskName,
                            String taskStatus, String requestPriority, String forecastStartDate, String forecastEndDate,
                            String actualStartDate, Integer slaDuration, String slaUnit, String processName, Integer processId,
                            String requestStatus, String slaStatus, String requester, String region, String district, String city) {
        this.taskId = taskId;
        this.requestId = requestId;
        this.siteId = siteId;
        this.customerSiteId = customerSiteId;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.requestPriority = requestPriority;
        this.forecastStartDate = forecastStartDate;
        this.forecastEndDate = forecastEndDate;
        this.actualStartDate = actualStartDate;
        this.slaDuration = slaDuration;
        this.slaUnit = slaUnit;
        this.processName = processName;
        this.processId = processId;
        this.requestStatus = requestStatus;
        this.slaStatus = slaStatus;
        this.requester = requester;
        this.region = region;
        this.district = district;
        this.city = city;
    }

    // Getters for all fields
    public int getTaskId() {
        return taskId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getCustomerSiteId() {
        return customerSiteId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public String getRequestPriority() {
        return requestPriority;
    }

    public String getForecastStartDate() {
        return forecastStartDate;
    }

    public String getForecastEndDate() {
        return forecastEndDate;
    }

    public String getActualStartDate() {
        return actualStartDate;
    }

    public Integer getSlaDuration() {
        return slaDuration;
    }

    public String getSlaUnit() {
        return slaUnit;
    }

    public String getProcessName() {
        return processName;
    }

    public Integer getProcessId() {
        return processId;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public String getSlaStatus() {
        return slaStatus;
    }

    public String getRequester() {
        return requester;
    }

    public String getRegion() {
        return region;
    }

    public String getDistrict() {
        return district;
    }

    public String getCity() {
        return city;
    }
}
