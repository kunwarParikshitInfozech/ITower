package com.isl.leaseManagement.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.isl.leaseManagement.room.entity.SubmitTaskRequest;


@Dao
public interface SubmitTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSubmitTask(SubmitTaskRequest submitTask);

    @Update
    void updateSubmitTask(SubmitTaskRequest submitTask);

    @Delete
    void deleteSubmitTask(SubmitTaskRequest submitTask);

    @Query("SELECT * FROM submit_task_table WHERE taskId = :taskId")
    SubmitTaskRequest getSubmitTaskById(int taskId);
}