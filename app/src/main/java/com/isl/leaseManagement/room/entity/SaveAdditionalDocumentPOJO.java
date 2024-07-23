package com.isl.leaseManagement.room.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "additional_document_table")
public class SaveAdditionalDocumentPOJO {

    @PrimaryKey
    @NonNull
    private final String docContentString64;
    private final String docName;
    private final String docSize;
    private final String docUploadId;
    private final Integer taskId; // Use Integer for nullable int

    public SaveAdditionalDocumentPOJO(String docContentString64, String docName, String docSize, String docUploadId, Integer taskId) {
        this.docContentString64 = docContentString64;
        this.docName = docName;
        this.docSize = docSize;
        this.docUploadId = docUploadId;
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

    public String getDocUploadId() {
        return docUploadId;
    }

    public Integer getTaskId() {
        return taskId;
    }
}
