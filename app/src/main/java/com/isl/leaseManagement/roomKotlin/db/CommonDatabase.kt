package com.isl.leaseManagement.roomKotlin.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocumentListConverter
import com.isl.leaseManagement.dataClasses.responses.BTSStartTaskAndWebCandidateResponse
import com.isl.leaseManagement.dataClasses.responses.BaladiyaNamesListResponse
import com.isl.leaseManagement.dataClasses.responses.CandidateDetailsAPIResponse
import com.isl.leaseManagement.dataClasses.responses.ExistingCandidateListResponse
import com.isl.leaseManagement.dataClasses.responses.ExistingCandidateListResponseConverter
import com.isl.leaseManagement.dataClasses.responses.FieldWorkDataConverter
import com.isl.leaseManagement.dataClasses.responses.FieldWorkStartTaskResponse
import com.isl.leaseManagement.roomKotlin.dao.BaladiyaNamesDao
import com.isl.leaseManagement.roomKotlin.dao.BtsCaptureCandidateStartResponseDao
import com.isl.leaseManagement.roomKotlin.dao.CandidateDetailsDao
import com.isl.leaseManagement.roomKotlin.dao.CapturedCandidateListResponseItemDao
import com.isl.leaseManagement.roomKotlin.dao.FieldWorkStartTaskResponseDao

@Database(
    entities = [FieldWorkStartTaskResponse::class, BaladiyaNamesListResponse.BaladiyaNamesListResponseItem::class,
        BTSStartTaskAndWebCandidateResponse::class, ExistingCandidateListResponse.ExistingCandidateListResponseItem::class,
        CandidateDetailsAPIResponse::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    FieldWorkDataConverter::class,
    SaveAdditionalDocumentListConverter::class,
    ExistingCandidateListResponseConverter::class,
    CandidateDetailsAPIResponse.AccountDetailConverter::class,
    CandidateDetailsAPIResponse.DelegateDetailConverter::class,
    CandidateDetailsAPIResponse.LandlordDetailConverter::class,
    CandidateDetailsAPIResponse.PropertyDetailsConverter::class
)
abstract class CommonDatabase : RoomDatabase() {

    abstract fun fieldWorkStartDao(): FieldWorkStartTaskResponseDao
    abstract fun baladiyaNameDao(): BaladiyaNamesDao
    abstract fun captureCandidateStartDao(): BtsCaptureCandidateStartResponseDao
    abstract fun existingCandidateListResponseItemDao(): CapturedCandidateListResponseItemDao
    abstract fun candidateDetailsDao(): CandidateDetailsDao

    companion object {
        @Volatile
        private var INSTANCE: CommonDatabase? = null

        fun getDatabase(context: Context): CommonDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CommonDatabase::class.java,
                    "common_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}