package com.isl.leaseManagement.roomCommon.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocumentListConverter
import com.isl.leaseManagement.dataClasses.responses.BaladiyaNamesListResponse
import com.isl.leaseManagement.dataClasses.responses.BtsCaptureCandidateStartDataConverter
import com.isl.leaseManagement.dataClasses.responses.BtsCaptureCandidateStartResponse
import com.isl.leaseManagement.dataClasses.responses.FieldWorkDataConverter
import com.isl.leaseManagement.dataClasses.responses.FieldWorkStartTaskResponse
import com.isl.leaseManagement.roomCommon.dao.BaladiyaNamesDao
import com.isl.leaseManagement.roomCommon.dao.BtsCaptureCandidateStartResponseDao
import com.isl.leaseManagement.roomCommon.dao.FieldWorkStartTaskResponseDao

@Database(
    entities = [FieldWorkStartTaskResponse::class, BaladiyaNamesListResponse.BaladiyaNamesListResponseItem::class,
        BtsCaptureCandidateStartResponse::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    FieldWorkDataConverter::class,
    SaveAdditionalDocumentListConverter::class,
    BtsCaptureCandidateStartDataConverter::class
)
abstract class CommonDatabase : RoomDatabase() {

    abstract fun fieldWorkStartDao(): FieldWorkStartTaskResponseDao
    abstract fun baladiyaNameDao(): BaladiyaNamesDao
    abstract fun captureCandidateStartDao(): BtsCaptureCandidateStartResponseDao

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
