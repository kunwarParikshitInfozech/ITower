package com.isl.modal;

public class NotificationListItem {
    private int id;
    private int userId;
    private String siteId;
    private String requestId;
    private String process;
    private String task;
    private String assignedTime;
    private String sla;
    private String notification;
    private int notificationType;
    private String subject;
    private boolean isRead;

    public NotificationListItem(int id, int userId, String siteId, String requestId,String process, String assignedTime, String task,
                                String sla, String notification,String subject,boolean isRead,int notificationType) {
        this.notificationType = notificationType;
        this.siteId = siteId;
        this.requestId = requestId;
        this.assignedTime = assignedTime;
        this.task = task;
        this.subject = subject;
        this.id = id;
        this.userId = userId;
        this.process = process;
        this.notification = notification;
        this.sla = sla;
        this.isRead = isRead;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setAssignedTime(String assignedTime) {
        this.assignedTime = assignedTime;
    }

    public void setSla(String sla) {
        this.sla = sla;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getProcess() {
        return process;
    }

    public String getTask() {
        return task;
    }

    public String getAssignedTime() {
        return assignedTime;
    }

    public String getSla() {
        return sla;
    }

    public String getNotification() {
        return notification;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public String getSubject() {
        return subject;
    }

    public boolean isRead() {
        return isRead;
    }
}
