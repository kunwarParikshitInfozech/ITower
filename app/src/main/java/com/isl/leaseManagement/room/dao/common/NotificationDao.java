package com.isl.leaseManagement.room.dao.common;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.isl.leaseManagement.room.entity.common.NotificationPOJO;

import java.util.List;

@Dao
public interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNotifications(NotificationPOJO notificationPOJO);

    @Update
    void updateNotification(NotificationPOJO notificationPOJO);

    @Delete
    void deleteNotification(NotificationPOJO notificationPOJO);

    @Query("SELECT * FROM notifications order by id desc")
    List<NotificationPOJO> getAllNotifications();

    @Query("SELECT * FROM notifications WHERE isRead = 0")
    List<NotificationPOJO> getUnreadNotifications();

    @Query("DELETE FROM notifications WHERE id = :id")
    int deleteNotificationByTaskId(int id);

    @Query("UPDATE notifications set isRead = 1,readTime=CURRENT_TIMESTAMP WHERE id = :id")
    void markAsReadById(int id);

    @Query("UPDATE notifications set isRead = 1,readTime=CURRENT_TIMESTAMP WHERE isRead = 0")
    void markAllAsRead();
}