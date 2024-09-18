package com.isl.leaseManagement.roomKotlin.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.isl.leaseManagement.dataClasses.responses.BtsCaptureCandidateStartResponse
import io.reactivex.Single

@Dao
interface BtsCaptureCandidateStartResponseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(response: BtsCaptureCandidateStartResponse): Single<Long>

    @Update
    fun update(response: BtsCaptureCandidateStartResponse): Single<Int>

    @Delete
    fun delete(response: BtsCaptureCandidateStartResponse): Single<Int>

    @Query("SELECT * FROM BtsCaptureCandidateStartResponse WHERE taskId = :currentTaskId")
    fun getBtsCaptureCandidateStartResponseByID(currentTaskId: Int): Single<BtsCaptureCandidateStartResponse>

    @Query("SELECT * FROM BtsCaptureCandidateStartResponse")
    fun getAllResponses(): Single<List<BtsCaptureCandidateStartResponse>>

    @Query("DELETE FROM BtsCaptureCandidateStartResponse WHERE taskId = :taskId")
    fun deleteByTaskId(taskId: Int): Single<Int>
}
