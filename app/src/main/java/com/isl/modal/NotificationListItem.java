package com.isl.modal;

public class NotificationListItem {
    String notificationType;
    String siteId;
    String requestId;
    String assignedTime;
    String task;
    String notificationSubject;

    public NotificationListItem(String notificationType, String siteId, String requestId, String assignedTime, String task, String notificationSubject) {
        this.notificationType = notificationType;
        this.siteId = siteId;
        this.requestId = requestId;
        this.assignedTime = assignedTime;
        this.task = task;
        this.notificationSubject = notificationSubject;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAssignedTime() {
        return assignedTime;
    }

    public void setAssignedTime(String assignedTime) {
        this.assignedTime = assignedTime;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getNotificationSubject() {
        return notificationSubject;
    }

    public void setNotificationSubject(String notificationSubject) {
        this.notificationSubject = notificationSubject;
    }



}
