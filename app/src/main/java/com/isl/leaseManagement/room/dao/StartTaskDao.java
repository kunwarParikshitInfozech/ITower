package com.isl.leaseManagement.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.isl.leaseManagement.room.entity.StartTaskResponsePOJO;


@Dao
public interface StartTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertStartTask(StartTaskResponsePOJO startTask);

    @Update
    void updateStartTask(StartTaskResponsePOJO startTask);

    @Delete
    void deleteStartTask(StartTaskResponsePOJO startTask);

    @Query("SELECT * FROM StartTaskResponsePOJO WHERE taskId = :taskId")
    StartTaskResponsePOJO getStartTaskById(int taskId);
}