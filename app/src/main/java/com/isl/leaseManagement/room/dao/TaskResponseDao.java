package com.isl.leaseManagement.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.isl.leaseManagement.room.entity.TaskResponse;


@Dao
public interface TaskResponseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTaskResponse(TaskResponse taskResponse);

    @Update
    void updateTaskResponse(TaskResponse taskResponse);

    @Delete
    void deleteTaskResponse(TaskResponse taskResponse);

    @Query("SELECT * FROM task_response_table WHERE taskId = :taskId")
    TaskResponse getTaskResponseById(int taskId);
}