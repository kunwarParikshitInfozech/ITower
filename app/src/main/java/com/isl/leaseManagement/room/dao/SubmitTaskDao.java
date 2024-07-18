package com.isl.leaseManagement.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.isl.leaseManagement.room.entity.SubmitTaskRequestPOJO;


@Dao
public interface SubmitTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSubmitTask(SubmitTaskRequestPOJO submitTask);

    @Update
    void updateSubmitTask(SubmitTaskRequestPOJO submitTask);

    @Delete
    void deleteSubmitTask(SubmitTaskRequestPOJO submitTask);

    @Query("SELECT * FROM SubmitTaskRequestPOJO WHERE taskId = :taskId")
    SubmitTaskRequestPOJO getSubmitTaskById(int taskId);
}