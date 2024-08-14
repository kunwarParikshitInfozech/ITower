package com.isl.leaseManagement.roomCommon.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.isl.leaseManagement.dataClasses.responses.FieldWorkStartTaskResponse
import io.reactivex.Single

@Dao
interface FieldWorkStartTaskResponseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(response: FieldWorkStartTaskResponse): Single<Long>

    @Update
    fun update(response: FieldWorkStartTaskResponse): Single<Int>

    @Delete
    fun delete(response: FieldWorkStartTaskResponse): Single<Int>

    @Query("SELECT * FROM FieldWorkStartTaskResponse WHERE taskId = :currentTaskId")
    fun getFieldWorkStartResponseByID(currentTaskId: Int): Single<FieldWorkStartTaskResponse>

    @Query("SELECT * FROM FieldWorkStartTaskResponse")
    fun getAllResponses(): Single<List<FieldWorkStartTaskResponse>>

    @Query("DELETE FROM FieldWorkStartTaskResponse WHERE taskId = :taskId")
    fun deleteByTaskId(taskId: String): Single<Int>
}

