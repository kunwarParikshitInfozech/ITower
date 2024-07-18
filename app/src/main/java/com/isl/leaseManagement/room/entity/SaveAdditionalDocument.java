package com.isl.leaseManagement.room.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "additional_document_table")
public class SaveAdditionalDocument {

    @PrimaryKey
    @NonNull
    private final String docContentString64;

    private final String docName;
    private final String docSize;
    private final String docUploadTime;
    private final Integer taskId; // Use Integer for nullable int

    public SaveAdditionalDocument(String docContentString64, String docName, String docSize, String docUploadTime, Integer taskId) {
        this.docContentString64 = docContentString64;
        this.docName = docName;
        this.docSize = docSize;
        this.docUploadTime = docUploadTime;
        this.taskId = taskId;
    }

    // Getters for all fields
    public String getDocContentString64() {
        return docContentString64;
    }

    public String getDocName() {
        return docName;
    }

    public String getDocSize() {
        return docSize;
    }

    public String getDocUploadTime() {
        return docUploadTime;
    }

    public Integer getTaskId() {
        return taskId;
    }
}
