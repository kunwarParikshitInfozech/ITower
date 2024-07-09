package com.isl.itower;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatDelegate;

import com.isl.leaseManagement.api.ApiClient;
import com.isl.leaseManagement.sharedPref.KotlinPrefkeeper;
import com.isl.leaseManagement.utils.LocalTempVarStore;

/**
 * Created by dhakan on 10/11/2018.
 */

public class MyApp extends Application {


    private static Context appContext;
    public static LocalTempVarStore localTempVarStore = LocalTempVarStore.INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //Utils.createFolder(AppConstants.MEDIA_TEMP_PATH);
        //Utils.createFolder(AppConstants.DOC_PATH);
        //Utils.createFolder(AppConstants.PIC_PATH);
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        final DataSyncReceiver myReceiver = new DataSyncReceiver();
        registerReceiver(myReceiver, filter);
        ApiClient.INSTANCE.init();
        KotlinPrefkeeper.init(this);
    }

    public static Context getAppContext() {
        return appContext;
    }


}