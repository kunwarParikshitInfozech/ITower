package com.isl.leaseManagement.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.isl.leaseManagement.room.entity.SaveAdditionalDocument;


@Dao
public interface SaveAdditionalDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDocument(SaveAdditionalDocument document);

    @Delete
    void deleteDocument(SaveAdditionalDocument document);

    @Query("SELECT * FROM additional_document_table WHERE docContentString64 = :docContentString64")
    SaveAdditionalDocument getDocumentByContentString(String docContentString64);
}