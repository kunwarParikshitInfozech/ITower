package com.isl.leaseManagement.room.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.isl.leaseManagement.room.dao.common.NotificationDao;
import com.isl.leaseManagement.room.dao.common.SaveAdditionalDocumentDao;
import com.isl.leaseManagement.room.dao.paymentProcess.StartTaskPaymentDao;
import com.isl.leaseManagement.room.dao.paymentProcess.SubmitTaskDao;
import com.isl.leaseManagement.room.dao.common.TaskResponseDao;
import com.isl.leaseManagement.room.entity.paymentProcess.StartTaskPaymentPOJO;
import com.isl.leaseManagement.room.entity.common.NotificationPOJO;
import com.isl.leaseManagement.room.entity.common.SaveAdditionalDocumentPOJO;
import com.isl.leaseManagement.room.entity.paymentProcess.SubmitTaskRequestPOJO;
import com.isl.leaseManagement.room.entity.common.TaskResponsePOJO;

@Database(
        entities = {TaskResponsePOJO.class, StartTaskPaymentPOJO.class, SaveAdditionalDocumentPOJO.class, SubmitTaskRequestPOJO.class,
                        NotificationPOJO.class},
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

    public abstract StartTaskPaymentDao startTaskDao();

    public abstract SaveAdditionalDocumentDao saveAdditionalDocumentDao();

    public abstract SubmitTaskDao submitTaskDao();

    public abstract NotificationDao notificationDao();
}
