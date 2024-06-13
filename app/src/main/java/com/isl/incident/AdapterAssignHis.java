package com.isl.incident;

import com.isl.modal.ResponceTabList;

import infozech.itower.R;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterAssignHis extends BaseAdapter {
    Context con;
    private LayoutInflater inflater = null;
    ResponceTabList list;
    int flag = 0;
    public AdapterAssignHis(Context con, ResponceTabList data,int flag) {
        this.con = con;
        this.list = data;
        this.flag = flag;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        int size = 0;
        if(flag == 0){
            size = list.getAssign().size();
        }
        if(flag == 1){
            size = list.getAlarm().size();
        }
        return size;
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public View getView(int position, View arg1, ViewGroup parent) {
        View vi = arg1;
        if (arg1 == null)
        vi = inflater.inflate(R.layout.list_item_remarks, null);
        TextView tv_update_time = (TextView) vi.findViewById(R.id.tv_update_time);
        TextView tv_update_by= (TextView) vi.findViewById(R.id.tv_update_by);
        TextView tv_update_field = (TextView) vi.findViewById(R.id.tv_update_field);
        TextView tv_update_doc = (TextView) vi.findViewById(R.id.tv_update_doc);
        TextView tv_alarm_telly = (TextView) vi.findViewById(R.id.tv_alarm_telly);
        TextView tv_alarm_detail = (TextView) vi.findViewById(R.id.tv_alarm_detail);
        // 0 for assign details 1 for alarm details
        if(flag == 0){
          tv_alarm_telly.setVisibility(View.GONE);
            tv_alarm_detail.setVisibility(View.GONE);
          if(list.getAssign().get(position).getASSIGNTO()!=null){
              tv_update_time.setText(Html.fromHtml("<b>Assigned To : </b> "+list.getAssign().get(position).getASSIGNTO()));

          }else{
              tv_update_time.setText(Html.fromHtml("<b>Assigned To : </b> "));
          }

          if(list.getAssign().get(position).getASSIGNED_DATE()!=null){
              tv_update_by.setText(Html.fromHtml("<b>Start Date Time : </b> "+list.getAssign().get(position).getASSIGNED_DATE()));
          }else{
              tv_update_by.setText(Html.fromHtml("<b>Start Date Time : </b> "));
          }

          if(list.getAssign().get(position).getASSIGNED_END_DATE()!=null){
              tv_update_field.setText(Html.fromHtml("<b>End Date Time : </b> "+list.getAssign().get(position).getASSIGNED_END_DATE()));
          }else{
              tv_update_field.setText(Html.fromHtml("<b>End Date Time : </b> "));
          }

          if(list.getAssign().get(position).getDURATION()!=null){
              tv_update_doc.setText(Html.fromHtml("<b>Duration : </b> "+list.getAssign().get(position).getDURATION()));
          }else{
              tv_update_doc.setText(Html.fromHtml("<b>Duration : </b> "));
          }
       }

       //TT-20200129-04457138

        if(flag == 1){
            tv_alarm_telly.setVisibility(View.VISIBLE);
          // tv_alarm_detail.setVisibility(View.VISIBLE);
           if(list.getAlarm().get(position).getFLAG().equalsIgnoreCase("1")){
               tv_alarm_detail.setVisibility(View.VISIBLE);

           }else{
               tv_alarm_detail.setVisibility(View.GONE);

           }

            if(list.getAlarm().get(position).getALARM_DESC()!=null){
                tv_update_time.setText(Html.fromHtml("<b>Alarm Description : </b> "+list.getAlarm().get(position).getALARM_DESC()));
            }else{
                tv_update_time.setText(Html.fromHtml("<b>Alarm Description : </b> "));
            }
            if(list.getAlarm().get(position).getALARM_DETAIL()!=null){
                tv_alarm_detail.setText(Html.fromHtml("<b>Alarm Detail : </b> "+list.getAlarm().get(position).getALARM_DETAIL()));
            }else{
                tv_alarm_detail.setText(Html.fromHtml("<b>Alarm Detail : </b> "));
            }

            if(list.getAlarm().get(position).getALARM_START_DATE()!=null){
                tv_update_by.setText(Html.fromHtml("<b>Alarm Start Date Time : </b> "+list.getAlarm().get(position).getALARM_START_DATE()));
            }else{
                tv_update_by.setText(Html.fromHtml("<b>Alarm Start Date Time : </b> "));
            }

            if(list.getAlarm().get(position).getLAST_OCCURRENCE_TIME()!=null){
                tv_update_field.setText(Html.fromHtml("<b>Last Occurence Time : </b> "+list.getAlarm().get(position).getLAST_OCCURRENCE_TIME()));
            }else{
                tv_update_field.setText(Html.fromHtml("<b>Last Occurence Time : </b> "));
            }

            if(list.getAlarm().get(position).getALARM_CLOSE_TIME()!=null){
                tv_update_doc.setText(Html.fromHtml("<b>Alarm End Date Time : </b> "+list.getAlarm().get(position).getALARM_CLOSE_TIME()));
            }else{
                tv_update_doc.setText(Html.fromHtml("<b>Alarm End Date Time : </b> "));
            }

            if(list.getAlarm().get(position).getALARM_TALLY()!=null){
                tv_alarm_telly.setText(Html.fromHtml("<b>Alarm Tally : </b> "+list.getAlarm().get(position).getALARM_TALLY()));
            }else{
                tv_alarm_telly.setText(Html.fromHtml("<b>Alarm Tally : </b> "));
            }
        }
       return vi;
    }
}