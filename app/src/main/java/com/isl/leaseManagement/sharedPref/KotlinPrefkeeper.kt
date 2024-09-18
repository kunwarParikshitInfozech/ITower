package com.isl.leaseManagement.sharedPref

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.isl.leaseManagement.dataClasses.responses.LocationsListResponse
import com.isl.leaseManagement.utils.AppConstants

object KotlinPrefkeeper {    //this class is used for saving data to shared preference
    private var prefs: SharedPreferences? = null

    @JvmStatic
    fun init(context: Context) {
        if (prefs == null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)

        }
    }

    var isOnDuty: Boolean
        get() = prefs!!.getBoolean(AppConstants.PrefsName.isOnDuty, false)
        set(isOnDuty) = prefs!!.edit().putBoolean(AppConstants.PrefsName.isOnDuty, isOnDuty)
            .apply()

    var lsmUserId: String?
        get() = prefs!!.getString(AppConstants.PrefsName.lsmUserId, "")
        set(lsmUserId) = prefs!!.edit().putString(AppConstants.PrefsName.lsmUserId, lsmUserId)
            .apply()

    var leaseManagementUserID: String?
        get() = prefs!!.getString(AppConstants.PrefsName.leaseManagementUserId, "1")
        set(leaseManagementUserId) = prefs!!.edit().putString(AppConstants.PrefsName.leaseManagementUserId, leaseManagementUserId)
            .apply()

    var deviceUUID: String?
        get() = prefs!!.getString(AppConstants.PrefsName.deviceUUID, "")
        set(deviceUUID) = prefs!!.edit().putString(AppConstants.PrefsName.deviceUUID, deviceUUID)
            .apply()

    var locationsList: List<LocationsListResponse.LocationsListResponseItem>?
        get() {
            val json = prefs!!.getString(AppConstants.PrefsName.locationList, null)
            return if (json != null) {
                val type = object : TypeToken<List<LocationsListResponse.LocationsListResponseItem>>() {}.type
                Gson().fromJson(json, type)
            } else {
                null
            }
        }
        set(locationsList) {
            val json = Gson().toJson(locationsList)
            prefs!!.edit().putString(AppConstants.PrefsName.locationList, json).apply()
        }

    fun clear() = prefs?.edit()?.clear()?.apply()


}