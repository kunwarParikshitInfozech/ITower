package com.isl.leaseManagement.roomKotlin.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.isl.leaseManagement.dataClasses.responses.ExistingCandidateListResponse.ExistingCandidateListResponseItem
import io.reactivex.Single

@Dao
interface ExistingCandidateListResponseItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(candidate: ExistingCandidateListResponseItem): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(candidates: List<ExistingCandidateListResponseItem>): Single<List<Long>>

    @Update
    fun update(candidate: ExistingCandidateListResponseItem): Single<Int>

    @Delete
    fun delete(candidate: ExistingCandidateListResponseItem): Single<Int>

    @Query("SELECT * FROM ExistingCandidateListResponseItem WHERE propertyId = :propertyId")
    fun getCandidateByPropertyId(propertyId: String): Single<ExistingCandidateListResponseItem>

    @Query("SELECT * FROM ExistingCandidateListResponseItem")
    fun getAllCandidates(): Single<List<ExistingCandidateListResponseItem>>

    @Query("DELETE FROM ExistingCandidateListResponseItem WHERE propertyId = :propertyId")
    fun deleteByPropertyId(propertyId: String): Single<Int>
}
