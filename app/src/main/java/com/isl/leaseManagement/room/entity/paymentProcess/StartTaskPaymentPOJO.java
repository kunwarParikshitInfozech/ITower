package com.isl.leaseManagement.room.entity.paymentProcess;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.gson.Gson; // Import Gson from Gson library

@Entity(tableName = "start_task_payment_table")
public class StartTaskPaymentPOJO {

    @PrimaryKey
    private final int taskId;

    private final String dataJson; // Store data as JSON string
    private final Integer processId; // Use Integer for nullable int

    public StartTaskPaymentPOJO(int taskId, String dataJson, Integer processId) {
        this.taskId = taskId;
        this.dataJson = dataJson;
        this.processId = processId;
    }

    // Getters for all fields
    public int getTaskId() {
        return taskId;
    }

    public String getDataJson() {
        return dataJson;
    }

    public Integer getProcessId() {
        return processId;
    }

    public static class StartTaskData {

        private final String accountNumber;
        private final String paymentMethod;
        private final String sadadBillerCode;

        public StartTaskData(String accountNumber, String paymentMethod, String sadadBillerCode) {
            this.accountNumber = accountNumber;
            this.paymentMethod = paymentMethod;
            this.sadadBillerCode = sadadBillerCode;
        }

        // Getters for all fields
        public String getAccountNumber() {
            return accountNumber;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public String getSadadBillerCode() {
            return sadadBillerCode;
        }
    }

    public static class StartTaskDataTypeConverter {

        private static final Gson gson = new Gson();

        @TypeConverter
        public static StartTaskData fromString(String dataJson) {
            if (dataJson != null) {
                return gson.fromJson(dataJson, StartTaskData.class);
            }
            return null;
        }

        @TypeConverter
        public static String toString(StartTaskData data) {
            return gson.toJson(data);
        }
    }
}
