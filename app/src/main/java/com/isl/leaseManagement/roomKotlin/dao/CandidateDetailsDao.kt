package com.isl.leaseManagement.roomKotlin.dao

import androidx.room.*
import com.isl.leaseManagement.base.BaseActivity
import com.isl.leaseManagement.dataClasses.responses.CandidateDetailsAPIResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

@Dao
interface CandidateDetailsDao {

    // Insert a single CandidateDetailsAPIResponse, replacing in case of conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCandidateDetails(candidateDetails: CandidateDetailsAPIResponse): Single<Long>

    // Insert multiple CandidateDetailsAPIResponse records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCandidateDetails(candidateDetailsList: List<CandidateDetailsAPIResponse>): Single<List<Long>>

    @Query("SELECT * FROM CandidateDetailsAPIResponse WHERE autoGeneratedId = :id")
    fun getCandidateDetailsByAutoGeneratedId(id: Int): Single<CandidateDetailsAPIResponse>

    // Update a specific CandidateDetailsAPIResponse
    @Update
    fun updateCandidateDetails(candidateDetails: CandidateDetailsAPIResponse): Single<Int>

    // Delete a specific CandidateDetailsAPIResponse
    @Delete
    fun deleteCandidateDetails(candidateDetails: CandidateDetailsAPIResponse): Single<Int>

    // Delete all CandidateDetailsAPIResponse records
    @Query("DELETE FROM CandidateDetailsAPIResponse")
    fun deleteAllCandidateDetails(): Single<Int>



}
private var disposable: Disposable? = null

fun getCandidateData(
    baseActivity: BaseActivity,
    autoGeneratedId: Int,
    callback: (CandidateDetailsAPIResponse?) -> Unit
) {
    val candidateDetailsDao = baseActivity.commonDatabase.candidateDetailsDao()
    disposable = candidateDetailsDao.getCandidateDetailsByAutoGeneratedId(autoGeneratedId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ candidateDetailsResponse ->
            callback(candidateDetailsResponse) // Returning the result via callback
        }, { error ->
            callback(null) // Returning null in case of error
        })
}

