package com.isl.leaseManagement.paymentProcess.activities.notification

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.isl.itower.MyApp
import com.isl.leaseManagement.room.db.MyDatabase
import com.isl.leaseManagement.utils.CustomTextView
import infozech.itower.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationDetailActivity : AppCompatActivity() {

    var txview_notification: CustomTextView? = null
    var txtview_siteid: CustomTextView? = null
    var txtview_requestid: CustomTextView? = null
    var txtview_task: CustomTextView? = null
    var db: MyDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        db = MyApp.getMyDatabase();
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_detail)
        txview_notification = findViewById(R.id.txview_notification)
        txtview_siteid = findViewById(R.id.txtview_siteid)
        txtview_requestid = findViewById(R.id.txtview_requestid)
        txtview_task = findViewById(R.id.txtview_task)
        var img_back: ImageView? = findViewById(R.id.img_back)
        img_back?.setOnClickListener(View.OnClickListener { finish() })
        intentValues
    }

    val intentValues: Unit
        get() {
            val intent = intent
            var id: Int = 0;

            if (intent != null) {
                val notificationSubject = intent.getStringExtra("subject")
                val siteId = intent.getStringExtra("siteid")!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val requestId = intent.getStringExtra("requestid")!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val task = intent.getStringExtra("task")!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                //txview_notification.setText(notificationSubject);
                id = intent.getStringExtra("id")!!.toInt()
                txtview_siteid!!.text = siteId[1]
                txtview_requestid!!.text = requestId[1]
                txtview_task!!.text = task[1]
            }

            if (id!=0 && id!=-1){
                lifecycleScope.launch(Dispatchers.IO) {
                    db?.notificationDao()?.markAsReadById(id);
                }
            }
        }
}
