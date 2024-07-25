package com.isl.leaseManagement.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isl.leaseManagement.activities.Notification.NotificationDetailActivity;
import com.isl.leaseManagement.utils.CustomTextView;
import com.isl.modal.NotificationListItem;

import java.util.ArrayList;
import java.util.List;

import infozech.itower.R;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    private Context context;
    ArrayList<NotificationListItem> listItem;

    public NotificationListAdapter(Context context, ArrayList<NotificationListItem> list) {
        this.context = context;
        this.listItem = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       NotificationListItem item = listItem.get(position);
       holder.txtview_notificationSubject.setText(item.getNotificationSubject());
       holder.txtview_siteid.setText(item.getSiteId());
       holder.txtview_date.setText(item.getAssignedTime());
       holder.imgview_next.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent intent = new Intent(context, NotificationDetailActivity.class);
              intent.putExtra("subject",item.getNotificationSubject());
              intent.putExtra("siteid",item.getSiteId());
              intent.putExtra("requestid",item.getRequestId());
              intent.putExtra("task",item.getTask());
              context.startActivity(intent);
          }
      });
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
         ImageView imgview_next;
         CustomTextView txtview_notificationSubject,txtview_siteid,txtview_date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgview_next = itemView.findViewById(R.id.imgview_next);
            txtview_notificationSubject = itemView.findViewById(R.id.txtview_notificationSubject);
            txtview_siteid = itemView.findViewById(R.id.txtview_siteid);
            txtview_date = itemView.findViewById(R.id.txtview_date);
        }

    }
}
