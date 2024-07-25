package com.isl.leaseManagement.activities.Notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.isl.leaseManagement.utils.CustomTextView;

import infozech.itower.R;

public class NotificationDetailActivity  extends AppCompatActivity {
    CustomTextView txview_notification,txtview_siteid,txtview_requestid,txtview_task;
    ImageView img_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);
        txview_notification = findViewById(R.id.txview_notification);
        txtview_siteid = findViewById(R.id.txtview_siteid);
        txtview_requestid = findViewById(R.id.txtview_requestid);
        txtview_task = findViewById(R.id.txtview_task);
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getIntentValues();
    }
    public void getIntentValues()
    {
        Intent intent = getIntent();
        if(intent != null)
        {
            String notificationSubject = intent.getStringExtra("subject");
            String[] siteId = intent.getStringExtra("siteid").split(":");
            String[] requestId = intent.getStringExtra("requestid").split(":");
            String[] task = intent.getStringExtra("task").split(":");
            txview_notification.setText(notificationSubject);
            txtview_siteid.setText(siteId[1]);
            txtview_requestid.setText(requestId[1]);
            txtview_task.setText(task[1]);
        }
    }
}
