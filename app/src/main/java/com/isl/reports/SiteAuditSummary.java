package com.isl.reports;
import infozech.itower.R;

import com.isl.constant.WebMethods;
import com.isl.modal.BeanReportsList;
import com.isl.dao.cache.AppPreferences;
import com.isl.util.Utils;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;

public class SiteAuditSummary extends Activity {
	ListView listView;
	ImageButton bt_search;
	EditText et_search;
	String searchData;
	BeanReportsList data_list;
	AppPreferences mAppPreferences;
	RelativeLayout ll_blank;
	ScrollView scrollView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.site_audit_summary);
		scrollView=(ScrollView)findViewById(R.id.scrollView);
		et_search=(EditText)findViewById(R.id.et_search);
		bt_search=(ImageButton)findViewById(R.id.bt_search);
		mAppPreferences = new AppPreferences(SiteAuditSummary.this);
		Button iv_back = (Button) findViewById(R.id.button_back);
		listView =(ListView)findViewById(R.id.listView1);
		LayoutInflater inflater = getLayoutInflater();
		View header = (View)inflater.inflate(R.layout.header_site_audit_summary,null);
		listView.addHeaderView(header);
		ll_blank = (RelativeLayout)findViewById(R.id.rl_blank);
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		if (Utils.isNetworkAvailable(SiteAuditSummary.this)) {
			new AssignedTicketsTask(SiteAuditSummary.this).execute();
		} else {
			Toast.makeText(SiteAuditSummary.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
		}
		
		 bt_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			searchData=et_search.getText().toString();
			if (Utils.isNetworkAvailable(SiteAuditSummary.this)) {
				new AssignedTicketsTask(SiteAuditSummary.this).execute();
			} else {
				Toast.makeText(SiteAuditSummary.this, "No Internet Connection",Toast.LENGTH_SHORT).show();
			}
			}
			});
		
	}
	@Override
	public void onBackPressed() {
		finish();
	}
	
	public class AssignedTicketsTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;
		Context con;
	
		public AssignedTicketsTask(Context con) {
			this.con = con;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(con, null, "Loading...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(11);
				nameValuePairs.add(new BasicNameValuePair("countryID",mAppPreferences.getCounrtyID()));
				nameValuePairs.add(new BasicNameValuePair("hubID",mAppPreferences.getHubID()));
				nameValuePairs.add(new BasicNameValuePair("regionID",mAppPreferences.getRegionId()));
				nameValuePairs.add(new BasicNameValuePair("circleID",mAppPreferences.getCircleID()));
				nameValuePairs.add(new BasicNameValuePair("zoneID",mAppPreferences.getZoneID()));
				nameValuePairs.add(new BasicNameValuePair("clusterID",mAppPreferences.getClusterID()));
				nameValuePairs.add(new BasicNameValuePair("omeID",""));
				nameValuePairs.add(new BasicNameValuePair("FromDate",""));
				nameValuePairs.add(new BasicNameValuePair("ToDate",""));
				nameValuePairs.add(new BasicNameValuePair("AuditBy",""));
				nameValuePairs.add(new BasicNameValuePair("SiteID",searchData));
				nameValuePairs.add(new BasicNameValuePair("EquipmentID",""));
				String response = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_GetEquipmentAuditSummary, nameValuePairs);
				Gson gson = new Gson();
				data_list = gson.fromJson(response,BeanReportsList.class);
			} catch (Exception e) {
				e.printStackTrace();
				data_list = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (!SiteAuditSummary.this.isFinishing()) {
				if ((data_list == null)) {
					Toast.makeText(SiteAuditSummary.this, "Server Not Available",Toast.LENGTH_LONG).show();
				} else if (data_list.getSite_Equipment_Audit_Summary().size() > 0) {
					ll_blank.setVisibility(View.GONE);
					scrollView.setVisibility(View.VISIBLE);
					setAdapter(listView, data_list);
				} else {
					Toast.makeText(SiteAuditSummary.this, "No Data Found",Toast.LENGTH_LONG).show();
					scrollView.setVisibility(View.GONE);
					ll_blank.setVisibility(View.VISIBLE);
				}
			}
			super.onPostExecute(result);
		}
	}

	public void setAdapter(ListView list, BeanReportsList data) {
		SiteAuditSummaryAdapter adapter = new SiteAuditSummaryAdapter(SiteAuditSummary.this,data);
		list.setDivider(null);
		list.setAdapter(adapter);
	}
}