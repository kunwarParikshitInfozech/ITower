package com.isl.leaseManagement.common.fragments.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isl.dao.cache.AppPreferences
import com.isl.itower.MyApp
import com.isl.leaseManagement.common.adapters.NotificationListAdapter
import com.isl.leaseManagement.base.BaseFragment
import com.isl.leaseManagement.room.db.MyDatabase
import com.isl.leaseManagement.room.entity.NotificationPOJO
import com.isl.modal.NotificationListItem
import infozech.itower.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LsmNotificationsFragment : BaseFragment() {
   lateinit var recyclerview_notification : RecyclerView
   lateinit var notificationListAdapter: NotificationListAdapter
   lateinit var mAppPreferences : AppPreferences
   var notificationListItem: java.util.ArrayList<NotificationListItem>? = null
   var db: MyDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        db = MyApp.getMyDatabase();
        var view = inflater.inflate(R.layout.fragment_lsm_notifications, container, false)
        recyclerview_notification = view.findViewById(R.id.recyclerview_notification)
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setClickListeners()
    }
    private fun setClickListeners() {
        val toolbar: View = requireActivity().findViewById(R.id.mark_all_read)
        toolbar.setOnClickListener {
            markAllAsRead()
            onResume()
        }
    }
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) {
            db?.notificationDao()?.allNotifications?.let { notificaionList ->
                val notificationListTmp = mutableListOf<NotificationListItem>()
                for (notificationPOJO in notificaionList) {
                    val notification = createNotificationDataClass(notificationPOJO)
                    notificationListTmp.add(notification)
                }

                withContext(Dispatchers.Main){
                    notificationListAdapter =
                        NotificationListAdapter(
                            context,
                            notificationListTmp.toList()
                        );
                    val layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
                    recyclerview_notification.layoutManager = layoutManager
                    recyclerview_notification.adapter = notificationListAdapter
                }

            }
        }
    }
    private fun createNotificationDataClass(notificationPOJO: NotificationPOJO): NotificationListItem {
        return NotificationListItem(
                notificationPOJO.id,
                notificationPOJO.userId,
                notificationPOJO.siteId,
                notificationPOJO.requestId,
                notificationPOJO.process,
                notificationPOJO.assignedTime,
                notificationPOJO.task,
                notificationPOJO.sla,
                notificationPOJO.notification,
                notificationPOJO.subject,
                notificationPOJO.isRead,
                notificationPOJO.notificationType);
    }

    private fun markAllAsRead(){
        lifecycleScope.launch(Dispatchers.IO) {
            db?.notificationDao()?.markAllAsRead()
        }
    }
}