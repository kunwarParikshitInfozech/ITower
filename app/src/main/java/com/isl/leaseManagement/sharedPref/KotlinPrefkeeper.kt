package com.isl.leaseManagement.sharedPref

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.isl.leaseManagement.dataClass.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.utils.AppConstants
import com.isl.workflow.constant.Constants.gson

object KotlinPrefkeeper {
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

    fun clear() = prefs?.edit()?.clear()?.apply()


}