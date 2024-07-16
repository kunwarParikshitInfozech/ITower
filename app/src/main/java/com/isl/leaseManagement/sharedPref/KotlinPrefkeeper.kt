package com.isl.leaseManagement.sharedPref

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.isl.leaseManagement.dataClass.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.dataClass.otherDataClasses.SaveAdditionalDocumentsArray
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


    var additionalDocIdsForSubmitApi: IntArray?
        get() {
            val json = prefs?.getString(AppConstants.PrefsName.ADDITIONAL_DOCUMENTS_DOC_ID, null)
            return if (json.isNullOrEmpty()) null else gson.fromJson(json, IntArray::class.java)
        }
        set(value) {
            val json = if (value != null) gson.toJson(value) else null
            prefs!!.edit().putString(AppConstants.PrefsName.ADDITIONAL_DOCUMENTS_DOC_ID, json)
                .apply()
        }

    var additionalDocDataArray: SaveAdditionalDocumentsArray?
        get() {
            val json = prefs?.getString(AppConstants.PrefsName.ADDITIONAL_DOCUMENTS_DOC_DATA_List, null)
            return if (json.isNullOrEmpty()) null else gson.fromJson(json, SaveAdditionalDocumentsArray::class.java)
        }
        set(value) {
            val json = if (value != null) gson.toJson(value) else null
            prefs!!.edit().putString(AppConstants.PrefsName.ADDITIONAL_DOCUMENTS_DOC_DATA_List, json)
                .apply()
        }

    fun clear() = prefs?.edit()?.clear()?.apply()


}