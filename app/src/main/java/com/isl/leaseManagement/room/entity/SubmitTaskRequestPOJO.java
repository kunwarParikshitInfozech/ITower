package com.isl.leaseManagement.room.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

@Entity(tableName = "submit_task_table")
@TypeConverters({SubmitTaskRequestPOJO.SubmitTaskDataConverter.class, SubmitTaskRequestPOJO.DocumentListConverter.class})
public class SubmitTaskRequestPOJO {

    @PrimaryKey
    private final int taskId;

    private final SubmitTaskData data;
    private final Integer processId; // Use Integer for nullable int

    public SubmitTaskRequestPOJO(int taskId, SubmitTaskData data, Integer processId) {
        this.taskId = taskId;
        this.data = data;
        this.processId = processId;
    }

    // Getters for all fields
    public int getTaskId() {
        return taskId;
    }

    public SubmitTaskData getData() {
        return data;
    }

    public Integer getProcessId() {
        return processId;
    }

    public static class SubmitTaskData {

        private final String accountNumber;
        private final List<Document> additionalDocuments;
        private final List<Document> documents;
        private final String paymentMethod;
        private final String rentVATExpiryDate;
        private final Integer sadadBillerCode; // Use Integer for nullable int
        private final String sadadExpiryDate;

        public SubmitTaskData(String accountNumber, List<Document> additionalDocuments, List<Document> documents,
                              String paymentMethod, String rentVATExpiryDate, Integer sadadBillerCode, String sadadExpiryDate) {
            this.accountNumber = accountNumber;
            this.additionalDocuments = additionalDocuments;
            this.documents = documents;
            this.paymentMethod = paymentMethod;
            this.rentVATExpiryDate = rentVATExpiryDate;
            this.sadadBillerCode = sadadBillerCode;
            this.sadadExpiryDate = sadadExpiryDate;
        }

        // Getters for all fields
        public String getAccountNumber() {
            return accountNumber;
        }

        public List<Document> getAdditionalDocuments() {
            return additionalDocuments;
        }

        public List<Document> getDocuments() {
            return documents;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public String getRentVATExpiryDate() {
            return rentVATExpiryDate;
        }

        public Integer getSadadBillerCode() {
            return sadadBillerCode;
        }

        public String getSadadExpiryDate() {
            return sadadExpiryDate;
        }
    }

    public static class Document {

        private final String docId;

        public Document(String docId) {
            this.docId = docId;
        }

        public String getDocId() {
            return docId;
        }
    }

    public static class SubmitTaskDataConverter {

        private static final Gson gson = new Gson();

        @TypeConverter
        public static SubmitTaskData fromString(String dataJson) {
            if (dataJson != null) {
                return gson.fromJson(dataJson, SubmitTaskData.class);
            }
            return null;
        }

        @TypeConverter
        public static String toString(SubmitTaskData data) {
            return gson.toJson(data);
        }
    }

    public static class DocumentListConverter {

        private static final Gson gson = new Gson();

        @TypeConverter
        public static List<Document> fromString(String dataJson) {
            if (dataJson != null) {
                return gson.fromJson(dataJson, new TypeToken<List<Document>>(){}.getType());
            }
            return Collections.emptyList();
        }

        @TypeConverter
        public static String toString(List<Document> documents) {
            return gson.toJson(documents);
        }
    }
}