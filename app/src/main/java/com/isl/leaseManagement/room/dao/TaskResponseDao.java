package com.isl.leaseManagement.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.isl.leaseManagement.room.entity.TaskResponsePOJO;

import java.util.List;


@Dao
public interface TaskResponseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTaskResponse(TaskResponsePOJO taskResponsePOJO);

    @Update
    void updateTaskResponse(TaskResponsePOJO taskResponsePOJO);

    @Delete
    void deleteTaskResponse(TaskResponsePOJO taskResponsePOJO);

    @Query("SELECT * FROM task_response_table WHERE taskId = :taskId")
    TaskResponsePOJO getTaskResponseById(int taskId);

    @Query("SELECT * FROM task_response_table")
    List<TaskResponsePOJO> getAllTaskResponse();
}