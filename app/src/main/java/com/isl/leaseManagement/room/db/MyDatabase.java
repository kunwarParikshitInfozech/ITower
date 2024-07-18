package com.isl.leaseManagement.room.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.isl.leaseManagement.room.dao.SaveAdditionalDocumentDao;
import com.isl.leaseManagement.room.dao.StartTaskDao;
import com.isl.leaseManagement.room.dao.SubmitTaskDao;
import com.isl.leaseManagement.room.dao.TaskResponseDao;
import com.isl.leaseManagement.room.entity.SaveAdditionalDocumentPOJO;
import com.isl.leaseManagement.room.entity.StartTaskResponsePOJO;
import com.isl.leaseManagement.room.entity.SubmitTaskRequestPOJO;
import com.isl.leaseManagement.room.entity.TaskResponsePOJO;

@Database(
        entities = {TaskResponsePOJO.class, StartTaskResponsePOJO.class, SaveAdditionalDocumentPOJO.class, SubmitTaskRequestPOJO.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({ /* Add your type converter classes here if needed */})
// This line might need adjustment
public abstract class MyDatabase extends RoomDatabase {

    private static MyDatabase instance;

    public static synchronized MyDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (MyDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, "my_database")
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract TaskResponseDao taskResponseDao();

    public abstract StartTaskDao startTaskDao();

    public abstract SaveAdditionalDocumentDao saveAdditionalDocumentDao();

    public abstract SubmitTaskDao submitTaskDao();
}
