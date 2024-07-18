package com.isl.leaseManagement.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.isl.leaseManagement.room.entity.SaveAdditionalDocumentPOJO;


@Dao
public interface SaveAdditionalDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDocument(SaveAdditionalDocumentPOJO document);

    @Delete
    void deleteDocument(SaveAdditionalDocumentPOJO document);

    @Query("SELECT * FROM SaveAdditionalDocumentPOJO WHERE docContentString64 = :docContentString64")
    SaveAdditionalDocumentPOJO getDocumentByContentString(String docContentString64);
}