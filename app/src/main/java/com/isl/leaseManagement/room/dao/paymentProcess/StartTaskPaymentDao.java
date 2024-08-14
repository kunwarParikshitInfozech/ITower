package com.isl.leaseManagement.room.dao.paymentProcess;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.isl.leaseManagement.room.entity.paymentProcess.StartTaskPaymentPOJO;


@Dao
public interface StartTaskPaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertStartTask(StartTaskPaymentPOJO startTask);

    @Update
    void updateStartTask(StartTaskPaymentPOJO startTask);

    @Delete
    void deleteStartTask(StartTaskPaymentPOJO startTask);

    @Query("SELECT * FROM start_task_payment_table WHERE taskId = :taskId")
    StartTaskPaymentPOJO getStartTaskById(int taskId);

    @Query("DELETE FROM start_task_payment_table WHERE taskId = :taskId")
    int deleteStartTaskByTaskId(int taskId);
}