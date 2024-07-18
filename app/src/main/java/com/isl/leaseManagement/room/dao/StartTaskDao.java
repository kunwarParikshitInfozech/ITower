package com.isl.leaseManagement.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.isl.leaseManagement.room.entity.StartTaskResponse;


@Dao
public interface StartTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertStartTask(StartTaskResponse startTask);

    @Update
    void updateStartTask(StartTaskResponse startTask);

    @Delete
    void deleteStartTask(StartTaskResponse startTask);

    @Query("SELECT * FROM start_task_table WHERE taskId = :taskId")
    StartTaskResponse getStartTaskById(int taskId);
}