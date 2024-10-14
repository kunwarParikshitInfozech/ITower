package com.isl.leaseManagement.roomKotlin.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.isl.leaseManagement.dataClasses.responses.BTSStartTaskAndWebCandidateResponse
import io.reactivex.Single

@Dao
interface BtsCaptureCandidateStartResponseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(response: BTSStartTaskAndWebCandidateResponse): Single<Long>

    @Update
    fun update(response: BTSStartTaskAndWebCandidateResponse): Single<Int>

    @Delete
    fun delete(response: BTSStartTaskAndWebCandidateResponse): Single<Int>

    @Query("SELECT * FROM BTSStartTaskAndWebCandidateResponse WHERE taskId = :currentTaskId")
    fun getBtsCaptureCandidateStartResponseByID(currentTaskId: Int): Single<BTSStartTaskAndWebCandidateResponse.CapturedCandidateFromWeb>

    @Query("SELECT * FROM BTSStartTaskAndWebCandidateResponse")
    fun getAllResponses(): Single<List<BTSStartTaskAndWebCandidateResponse>>

    @Query("DELETE FROM BTSStartTaskAndWebCandidateResponse WHERE taskId = :taskId")
    fun deleteByTaskId(taskId: Int): Single<Int>
}
