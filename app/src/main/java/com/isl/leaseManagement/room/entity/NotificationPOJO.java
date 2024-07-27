package com.isl.leaseManagement.room.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "notifications")
public class NotificationPOJO {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private final int userId;
    private final String siteId;
    private final String requestId;
    private final String process;
    private final String task;
    private final String assignedTime;
    private final String sla;
    private final String notification;
    private final String subject;
    private final int notificationType;
    private final boolean isRead;
    private final long readTime;

    public NotificationPOJO(int userId, String notification, String siteId, String requestId, String task, String assignedTime,
                            String process, String sla, int notificationType, String subject, boolean isRead,long readTime) {
        //this.id = id;
        this.userId = userId;
        this.notification = notification;
        this.notificationType = notificationType;
        this.isRead = isRead;
        this.siteId = siteId;
        this.requestId = requestId;
        this.task = task;
        this.assignedTime = assignedTime;
        this.process = process;
        this.sla = sla;
        this.subject = subject;
        this.readTime = readTime;

    }

    public long getReadTime() {
        return readTime;
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

    public String getSubject() {
        return subject;
    }

    public String getAssignedTime() {
        return assignedTime;
    }

    public String getSla() {
        return sla;
    }

    public boolean isRead() {
        return isRead;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getNotification() {
        return notification;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setId(int id) {
        this.id = id;
    }
}
