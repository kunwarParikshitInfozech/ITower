package com.isl.leaseManagement.fragments.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.isl.dao.cache.AppPreferences
import com.isl.leaseManagement.adapters.NotificationListAdapter
import com.isl.leaseManagement.base.BaseFragment
import com.isl.modal.NotificationListItem
import infozech.itower.R

class LsmNotificationsFragment : BaseFragment() {
   lateinit var recyclerview_notification : RecyclerView
   lateinit var notificationListAdapter: NotificationListAdapter
   lateinit var mAppPreferences : AppPreferences
    var notificationListItem: java.util.ArrayList<NotificationListItem>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_lsm_notifications, container, false)
        recyclerview_notification = view.findViewById(R.id.recyclerview_notification)
        mAppPreferences = AppPreferences(context)
        notificationListItem = java.util.ArrayList()
        val gson = Gson()
        if (mAppPreferences.notificationList != null) {
            notificationListItem = gson.fromJson(
                mAppPreferences.notificationList,
                object : TypeToken<ArrayList<NotificationListItem?>?>() {}.type
            )
            notificationListAdapter = NotificationListAdapter(context,notificationListItem)
            val layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            recyclerview_notification.layoutManager = layoutManager
            recyclerview_notification.adapter = notificationListAdapter
        }

        return view;
    }



}