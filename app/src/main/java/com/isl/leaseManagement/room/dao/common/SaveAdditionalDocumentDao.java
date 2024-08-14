package com.isl.leaseManagement.room.dao.common;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.isl.leaseManagement.room.entity.common.SaveAdditionalDocumentPOJO;

import java.util.List;


@Dao
public interface SaveAdditionalDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDocument(SaveAdditionalDocumentPOJO document);

    @Delete
    void deleteDocument(SaveAdditionalDocumentPOJO document);

    @Query("SELECT * FROM additional_document_table WHERE taskId = :taskId")
    List<SaveAdditionalDocumentPOJO> getAllSavedDocumentsOfATask(String taskId);

    @Query("DELETE FROM additional_document_table WHERE taskId = :taskId")
    int deleteAllDocumentsOfTask(int taskId);
}