package com.isl.leaseManagement.roomCommon.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.isl.leaseManagement.dataClasses.responses.BaladiyaNamesListResponse
import io.reactivex.Single

@Dao
interface BaladiyaNamesDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(item: BaladiyaNamesListResponse.BaladiyaNamesListResponseItem): Single<Long>

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(list: List<BaladiyaNamesListResponse.BaladiyaNamesListResponseItem>): Single<List<Long>>


//    @Query("DELETE FROM BaladiyaNamesListResponseItem")
//    suspend fun deleteAll(): Int

//    @Query("SELECT * FROM BaladiyaNamesListResponseItem")
//    suspend fun getAllBaladiyaNames(): Single<List<BaladiyaNamesListResponse.BaladiyaNamesListResponseItem>>
//
//    @Query("SELECT * FROM BaladiyaNamesListResponseItem WHERE baladiyaId = :id")
//    suspend fun getBaladiyaNameById(id: Int): Single<BaladiyaNamesListResponse.BaladiyaNamesListResponseItem?>
}